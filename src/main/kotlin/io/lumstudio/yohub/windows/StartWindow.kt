package io.lumstudio.yohub.windows

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.google.gson.Gson
import io.lumstudio.yohub.R
import io.lumstudio.yohub.common.*
import io.lumstudio.yohub.common.shell.KeepShellStore
import io.lumstudio.yohub.common.shell.LocalKeepShell
import io.lumstudio.yohub.common.utils.ColorLoader
import io.lumstudio.yohub.common.utils.LocalPreferences
import io.lumstudio.yohub.common.utils.PreferencesName
import io.lumstudio.yohub.common.utils.PreferencesStore
import io.lumstudio.yohub.runtime.*
import io.lumstudio.yohub.theme.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import org.jetbrains.skiko.hostOs

@Composable
fun StartWindow() {

    val isFinishedAnimatable = remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    val floatState = animateFloatAsState(
        targetValue = if (isFinishedAnimatable.value) 0f else 1f,
        animationSpec = tween(),
        finishedListener = {
            isFinished = true
        }
    )

    val windowState = rememberWindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize(300.dp, 200.dp)
    )

    val contextStore = remember { ContextStore() }
    val preference = remember { mutableStateMapOf<String, String?>() }
    val preferencesStore = remember { PreferencesStore(contextStore.rootDir, preference) }

    val colorThemeStore = remember { ColorThemeStore(ColorThemeStore.ColorTheme(LightColorScheme, DarkColorScheme)) }
    val installThemesPathStore = remember { InstallThemesPathStore(contextStore.fileDir) }

    //监听窗口变化
    LaunchedEffect(floatState) {
        snapshotFlow { floatState.value }
            .onEach { windowState.size = DpSize(400.dp, 300.dp) * it }
            .launchIn(this)
    }

    val coroutineScope = remember { IOCoroutine() }
    val runtimeStore = remember { RuntimeStore(contextStore.rootDir) }
    val deviceStore = remember { DeviceStore() }
    val adbRuntimeStore = remember { AdbStore(runtimeStore.runtimeFile, deviceStore) }
    val pythonStore = remember { PythonStore(runtimeStore.runtimeFile) }
    val payloadDumperStore = remember { PayloadDumperStore(runtimeStore.runtimeFile) }
    val magiskPatcherStore = remember { MagiskPatcherStore(pythonStore.pythonHostFile) }

    val driverStore = remember { DriverStore() }

    val keepShellStore = remember {
        KeepShellStore(
            runtimeStore.runtimeFile.absolutePath,
            adbRuntimeStore,
            pythonStore,
            payloadDumperStore,
        )
    }

    val fastbootDriverStore = remember { FastbootDriverStore(runtimeStore.runtimeFile, keepShellStore) }

    val devices = remember { mutableStateListOf<Device>() }
    val devicesStore = remember { DevicesStore(devices) }

    //加载设备
    InputDevicesWatcher(
        onChange = {
            loadAndroidDevices(
                keepShellStore = keepShellStore,
                adbRuntimeStore = adbRuntimeStore,
                deviceStore = deviceStore,
                devicesStore = devicesStore,
                driverStore = driverStore
            )
        },
        true
    )

    //初始化全局配置
    CompositionLocalProvider(
        LocalIOCoroutine provides coroutineScope,
        LocalContext provides contextStore,
        LocalPreferences provides preferencesStore,
        LocalRuntime provides runtimeStore,
        LocalDevice provides deviceStore,
        LocalAdbRuntime provides adbRuntimeStore,
        LocalPythonRuntime provides pythonStore,
        LocalPayloadDumperRuntime provides payloadDumperStore,
        LocalMagiskPatcherRuntime provides magiskPatcherStore,
        LocalFastbootDriverRuntime provides fastbootDriverStore,
        LocalKeepShell provides keepShellStore,
        LocalDevices provides devicesStore,
        LocalDriver provides driverStore,
        LocalColorTheme provides colorThemeStore,
        LocalInstallThemesPath provides installThemesPathStore,
    ) {
        InitProperties(preferencesStore)
        if (!isFinished) {
            LoadUI(isFinishedAnimatable, windowState)
        } else {
            MainUI()
        }
    }
}

private val gson by lazy { Gson() }

/**
 * 初始化配置信息
 */
@Composable
private fun InitProperties(preferencesStore: PreferencesStore) {
    val installThemesPathStore = LocalInstallThemesPath.current
    val colorThemeStore = LocalColorTheme.current
    val colorLoader = ColorLoader(preferencesStore, installThemesPathStore, colorThemeStore)
    LaunchedEffect(preferencesStore.preference) {
        //初始化配置
        preferencesStore.loadPreference()
        val localhostColorTheme = preferencesStore.preference[PreferencesName.COLOR_THEME.toString()]

        if (installThemesPathStore.installPathFile.exists() && !localhostColorTheme.isNullOrEmpty() && localhostColorTheme != "YoHub Color") {
            try {
                colorThemeStore.colorSchemes = colorLoader.loadInstalledColorTheme(localhostColorTheme)
            }catch (e: Exception) {
                e.printStackTrace()
                sendNotice("主题颜色加载失败！", "${e.message}，已将主题颜色设为默认。")
                preferencesStore.preference[PreferencesName.COLOR_THEME.toString()] = "YoHub Color"
                preferencesStore.submit()
            }
        } else {
            //初始化默认主题
            preferencesStore.preference[PreferencesName.COLOR_THEME.toString()] = "YoHub Color"
            preferencesStore.submit()
            colorThemeStore.colorSchemes = ColorThemeStore.ColorTheme(LightColorScheme, DarkColorScheme)
        }
    }
}

/**
 * 初始化窗口
 */
@Composable
private fun LoadUI(isFinishedAnimatable: MutableState<Boolean>, windowState: WindowState) {
    val applicationScope = LocalApplication.current
    Window(
        title = "优画工具箱桌面版",
        icon = painterResource(R.icon.logoRound),
        onCloseRequest = applicationScope::exitApplication,
        state = windowState,
        resizable = false,
        undecorated = true,
        transparent = true
    ) {
        MicaTheme {
            val tipText = remember { mutableStateOf("") }
            var logoAnimateState by remember { mutableStateOf(false) }
            WindowDraggableArea(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {

                    LaunchedEffect(logoAnimateState) {
                        delay(100)
                        logoAnimateState = true
                    }

                    AnimatedVisibility(
                        visible = logoAnimateState,
                        modifier = Modifier.align(Alignment.Center),
                        enter = fadeIn(tween(durationMillis = 1250))
                    ) {
                        Image(
                            painterResource(R.icon.logoRound),
                            null,
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        Text(
                            text = tipText.value,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(end = 16.dp, bottom = 4.dp)
                        )
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                            trackColor = Color.Transparent
                        )
                    }
                }
                LoadConfigurations(isFinishedAnimatable, tipText)
            }
        }
    }
}

/**
 * 加载系统配置
 * @param isFinishedAnimatable 是否完成加载
 * @param tipText 提示文本
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
private fun LoadConfigurations(
    isFinishedAnimatable: MutableState<Boolean>,
    tipText: MutableState<String>
) {
    val adbStore = LocalAdbRuntime.current
    val pythonStore = LocalPythonRuntime.current
    val payloadDumperStore = LocalPayloadDumperRuntime.current
    val magiskPatcherStore = LocalMagiskPatcherRuntime.current
    val fastbootDriverStore = LocalFastbootDriverRuntime.current

    LaunchedEffect(isFinishedAnimatable) {
        withContext(
            CoroutineExceptionHandler { _, throwable ->
            } + Dispatchers.IO
        ) {
            tipText.value = "准备中..."
            delay(1000)
            run {
                tipText.value = "开始初始化ADB环境..."
                adbStore.installRuntime(
                    resource(adbStore.resourceName).readBytes(),
                    adbStore.adbHostFile.absolutePath
                )
            }
            run {
                tipText.value = "开始初始化Python环境..."
                pythonStore.installRuntime(
                    resource(pythonStore.resourceName).readBytes(),
                    pythonStore.pythonHostFile.absolutePath
                )
            }
            run {
                tipText.value = "开始初始化Payload Dumper环境..."
                payloadDumperStore.installRuntime(
                    resource(payloadDumperStore.resourceName).readBytes(),
                    payloadDumperStore.payloadHostFile.absolutePath
                )
            }
            run {
                tipText.value = "开始初始化Magisk Patcher环境..."
                magiskPatcherStore.installRuntime(
                    resource(magiskPatcherStore.resourceName).readBytes(),
                    magiskPatcherStore.magiskPatcherHostFile.absolutePath
                )
            }
            run {
                tipText.value = "开始初始化Android驱动..."
                fastbootDriverStore.installRuntime(
                    resource(fastbootDriverStore.resourceName).readBytes(),
                    fastbootDriverStore.fastbootDriverHostFile.absolutePath
                )
            }
            tipText.value = "加载完成！"
            delay(1000)
            isFinishedAnimatable.value = true
        }
    }
}

fun loadAndroidDevices(
    delay: Long = 1500,
    keepShellStore: KeepShellStore,
    adbRuntimeStore: AdbStore,
    deviceStore: DeviceStore,
    devicesStore: DevicesStore,
    driverStore: DriverStore,
    onFinished: (() -> Unit)? = null
) {
    CoroutineScope(Dispatchers.IO).launch {
        delay(delay)
        checkFastbootDriver(keepShellStore, driverStore)
        devicesStore.devices.clear()
        val adbOut = keepShellStore adb "devices"
        if (adbOut.contains("kill-server")) {
            keepShellStore adb "kill-server"
            loadAndroidDevices(
                keepShellStore = keepShellStore,
                adbRuntimeStore = adbRuntimeStore,
                deviceStore = deviceStore,
                devicesStore = devicesStore,
                driverStore = driverStore
            )
        }
        adbOut.split("\n").filter { it.contains("\t") }
            .onEach {
                try {
                    val key = "\t"
                    val id = it.substring(0, it.indexOf(key))
                    val state = ClientState.valueOf(it.substring(it.lastIndexOf(key) + 1, it.length).uppercase())
                    val type =
                        if (keepShellStore.cmd(adbRuntimeStore.adbDevice(id, "shell getprop ro.virtual_ab.enabled"))
                                .contains("true")
                        ) {
                            ClientType.ADB_VAB
                        } else if (keepShellStore.cmd(
                                adbRuntimeStore.adbDevice(
                                    id,
                                    "shell getprop ro.boot.slot_suffix"
                                )
                            ).contains("_")
                        ) {
                            ClientType.ADB_AB
                        } else {
                            ClientType.ADB
                        }
                    val device = Device(id.trim(), state, type)
                    if (devicesStore.devices.none { element -> element.id == device.id }) {
                        devicesStore.devices.add(device)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        val fastbootOut = keepShellStore fastboot "devices"
        fastbootOut.split("\n").filter { it.contains("\t") }
            .onEach {
                try {
                    val id = it.substring(0, it.indexOf("\t"))
                    val device = Device(id.trim(), ClientState.FASTBOOT, ClientType.FASTBOOT)
                    if (devicesStore.devices.none { element -> element.id == device.id }) {
                        devicesStore.devices.add(device)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        //自动连接设备
        if (deviceStore.device == null && devicesStore.devices.size == 1) {
            deviceStore.device = devicesStore.devices.first()
        }
        //设备更改后检测原先已选择的设备是否被拔出
        if (devicesStore.devices.none { it.id == deviceStore.device?.id }) {
            deviceStore.device = null
        }
        onFinished?.invoke()
    }
}

/**
 * 检查驱动安装情况
 */
fun checkFastbootDriver(keepShellStore: KeepShellStore, driverStore: DriverStore) {
    when {
        hostOs.isWindows -> {
            val drivers =
                keepShellStore cmd "reg query HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services"
            driverStore.isInstall = drivers.split("\n").any { it.uppercase().contains("WINUSB".uppercase()) }
        }

        else -> {}
    }
}
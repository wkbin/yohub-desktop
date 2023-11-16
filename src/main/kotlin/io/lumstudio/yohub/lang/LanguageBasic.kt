﻿package io.lumstudio.yohub.lang

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

val LocalLanguageType: MutableState<LanguageType> by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    mutableStateOf(LanguageType.DEFAULT)
}

/**
 * 已适配的语言
 */
enum class LanguageType(
    val languageName: String,
    var lang: LanguageBasic
) {
    DEFAULT("", LanguageZHCN),
    ZH_CN("简体中文", LanguageZHCN),
    EN("English", LanguageEN),
}

/**
 * 国际化语言词典
 */
abstract class LanguageBasic(
    val appName: String,
    val inPreparation: String,
    val initAdbRuntime: String,
    val initPythonRuntime: String,
    val initPayloadDumper: String,
    val initMagiskPatcher: String,
    val initAdbDriver: String,
    val initAndroidToolkit: String,
    val finished: String,
    val themeColorLoadFailedTitle: String,
    val themeColorLoadFailedMessage: String,
    val searchFunctions: String,
    val tooltipTextDevice: String,
    val unlinkDevice: String,
    val refreshDeviceList: String,
    val checkIndexDevice: String,
    val deviceType: String,
    val deviceTypeUnAuthorization: String,
    val unknownDevice: String,
    val unknown: String,
    val driverState: String,
    val normal: String,
    val exception: String,
    val notChooseDevice: String,
    val linkedDevice: String,
    val tips: String,
    val defined: String,
    val cancel: String,
    val dialogRebootDevice: String,
    val rebootDevice: String,
    val dialogShutdownDevice: String,
    val shutdownDevice: String,
    val dialogRebootBootloaderDevice: String,
    val rebootBootloaderDevice: String,
    val dialogRebootRecoveryDevide: String,
    val rebootRecoveryDevide: String,
    val labelHome: String,
    val labelPayload: String,
    val titlePayload: String,
    val subtitlePayload: String,
    val labelMagiskArea: String,
    val titleMagiskArea: String,
    val subtitleMagiskArea: String,
    val labelMagiskPatcher: String,
    val titleMagiskPatcher: String,
    val subtitleMagiskPatcher: String,
    val labelMagiskRepository: String,
    val labelAdbArea: String,
    val titleAdbArea: String,
    val subtitleAdbArea: String,
    val labelAdbInstaller: String,
    val labelAdbPicker: String,
    val labelAdbActiveArea: String,
    val labelSettings: String,
    val labelFlashImage: String,
    val titleFlashImage: String,
    val subtitleFlashImage: String,
    val labelUnlink: String,
    val titleUnlink: String,
    val labelFlashImageLinked: String,
    val labelTheme: String,
    val labelVersion: String,
    val labelOpenSourceLicense: String,
    val noticeTitleActiveSuccess: String,
    val noticeMessageActiveSuccess: String,
    val noticeTitleActiveFail: String,
    val activeShizuku: String,
    val noticeTitleSuccessBlackScope: String,
    val activeBlackScope: String,
    val appList: String,
    val findApps: String,
    val inputSaveApkPath: String,
    val chooseFile: String,
    val chooseDir: String,
    val openFileManager: String,
    val searchApps: String,
    val all: String,
    val system: String,
    val user: String,
    val pickApp: String,
    val noticePickAppSuccess: String,
    val noticePickAppFail:String,
    val noticeMessagePickAppFail:String,
    val startInstall: String,
    val notChooseApk: String,
    val inputApkPath: String,
    val chooseFail: String,
    val chooseFailMessage: String,
    val apkInstalling: String,
    val installSuccess: String,
    val installSuccessMessage: String,
    val installFail: String,
    val pleaseLinkDevice: String,
    val pleaseChooseDevice: String,
    val pleaseAuthorizeDevice: String,
    val pleaseLinkAdbDevice: String,
    val linkedAdbDevice: String,
    val loading: String,
    val deviceInfo: String,
    val adbAreaFunctions: String,
    val insufficientPermissions: String,
    val adbPhoneInfo: String,
    val externalStorageInfo: String,
    val externalStorageSpace: String,
    val memorySpace: String,
    val swapSpace: String,
    val coreLoad: String,
    val flashImage: String,
    val flashConfirm: String,
    val flashMessage: String,
    val flashing: String,
    val flashSuccess: String,
    val flashSuccessMessage: String,
    val flashFail: String,
    val inputImagePath: String,
    val choosePartition: String,
    val partitionSelected: String,
    val aBSlotFilter: String,
    val textChoosePartition: String,
    val startPatcherImage: String,
    val notChooseBoot: String,
    val inputBootPath: String,
    val bootOutputPath: String,
    val bootPatching: String,
    val patchSuccess: String,
    val patchSuccessMessage: String,
    val patchFail: String,
    val searchMagiskVersion: String,
    val magiskList: String,
    val fastLoading: String,
    val magiskVersionSubtitle: String,
    val downloadApk: String,
    val lineOne: String,
    val lineTwo: String,
    val loadFailAndRetry: String,
    val imageList: String,
    val notChoosePayloadFile: String,
    val downloadMIUIRom: String,
    val inputPayloadPath: String,
    val imageOutputPath: String,
    val searchImage: String,
    val refreshList: String,
    val findImageCount: String,
    val pickImage: String,
    val imagePickedSaveAt: String,
    val appCopyright: String,
    val darkMode: String,
    val themeColor: String,
    val generateThemeFile: String,
    val uninstallFail: String,
    val uninstallFailMessage: String,
    val uninstallThemeTip: String,
    val generateTheme: String,
    val analysisFail: String,
    val pleaseInputColorFilePath: String,
    val themeAnalysisFail: String,
    val isNotEffectiveColorFile: String,
    val themeCreateFail: String,
    val themeNameCannotEmpty: String,
    val themeGenerateSuccess: String,
    val themeGenerateSuccessMessage: String,
    val themeGenerateError: String,
    val themeGenerateFail: String,
    val targetFileIsNotExists: String,
    val help: String,
    val iKnown: String,
    val getColorFile: String,
    val generateColorThemeMessage: String,
    val themeName: String,
    val colorFilePath: String,
    val installTheme: String,
    val openSourceLicenseUrl: String,
    val gotoUrl: String,
    val themeAnalysisError: String,
    val themeAnalysisMessage: String,
    val installThemeSuccess: String,
    val themeAnalysisFailMessage: String,
    val uninstallSuccess: String,
    val uninstallSuccessMessage: String,
    val themeFileIsNotExists: String,
    val darkModeSystem: String,
    val darkModeLight: String,
    val darkModeDark: String,
    val language: String,
    val languageSetting: String,
    val defaultLanguage: String,
    val appInfoFormat: String,
    val labelAdvancedFunction: String,
    val rootFunction: String,
    val labelImageBackup: String,
    val checkRootTip: String,
    val notRootAndRetry: String,
    val labelAdvancedSetting: String,
    val adminCode: String,
    val adminCodeSubtitle: String,
    val imageBackupPath: String,
    val partitionList: String,
    val searchPartition: String,
    val selectAll: String,
    val selectNone: String,
    val findPartitions: String,
    val textBackupPartitions: String,
    val backupFail: String,
    val noSelectPartitionMessage: String,
    val backupTaskTitle: String,
    val backupTaskMessage: String,
    val backupImageCancel: String,
    val backupImageCancelMessage: String,
    val tryCancel: String,
    val backupFolderIsNotExists: String,
    val backupFinished: String,
    val backupFinishedMessage: String,
    val confirmBackupImage: String,
    val backupAllImage: String,
)


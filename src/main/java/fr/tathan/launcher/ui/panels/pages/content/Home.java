package fr.tathan.launcher.ui.panels.pages.content;

import fr.flowarg.flowupdater.download.json.CurseModPackInfo;
import fr.flowarg.flowupdater.download.json.OptiFineInfo;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.tathan.launcher.Launcher;
import fr.tathan.launcher.game.MinecraftInfos;
import fr.tathan.launcher.ui.PanelManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;
import fr.flowarg.flowupdater.download.json.CurseFileInfo;
import fr.flowarg.flowupdater.download.json.Mod;
import fr.flowarg.flowupdater.utils.UpdaterOptions;
import fr.flowarg.flowupdater.versions.AbstractForgeVersion;
import fr.flowarg.flowupdater.versions.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.*;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Home extends ContentPanel {
    private final Saver saver = Launcher.getInstance().getSaver();
    GridPane boxPane = new GridPane();
    ProgressBar progressBar = new ProgressBar();
    Label stepLabel = new Label();
    Label fileLabel = new Label();
    boolean isDownloading = false;

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/home.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        rowConstraints.setMinHeight(75);
        rowConstraints.setMaxHeight(75);
        this.layout.getRowConstraints().addAll(rowConstraints, new RowConstraints());
        boxPane.getStyleClass().add("box-pane");
        setCanTakeAllSize(boxPane);
        boxPane.setPadding(new Insets(20));
        this.layout.add(boxPane, 0, 0);
        this.layout.getStyleClass().add("home-layout");

        progressBar.getStyleClass().add("download-progress");
        stepLabel.getStyleClass().add("download-status");
        fileLabel.getStyleClass().add("download-status");

        progressBar.setTranslateY(-15);
        setCenterH(progressBar);
        setCanTakeAllWidth(progressBar);

        stepLabel.setTranslateY(5);
        setCenterH(stepLabel);
        setCanTakeAllSize(stepLabel);

        fileLabel.setTranslateY(20);
        setCenterH(fileLabel);
        setCanTakeAllSize(fileLabel);

        this.showPlayButton();
    }

    private void showPlayButton() {
        boxPane.getChildren().clear();
        Button playBtn = new Button("Jouer");
        FontAwesomeIconView playIcon = new FontAwesomeIconView(FontAwesomeIcon.GAMEPAD);
        playIcon.getStyleClass().add("play-icon");
        setCanTakeAllSize(playBtn);
        setCenterH(playBtn);
        setCenterV(playBtn);
        playBtn.getStyleClass().add("play-btn");
        playBtn.setGraphic(playIcon);
        playBtn.setOnMouseClicked(e -> this.play());
        boxPane.getChildren().add(playBtn);
    }

    private void play() {
        isDownloading = true;
        boxPane.getChildren().clear();
        setProgress(0, 0);
        boxPane.getChildren().addAll(progressBar, stepLabel, fileLabel);

        Platform.runLater(() -> new Thread(this::update).start());
    }

    public void update() {

        IProgressCallback callback = new IProgressCallback() {
            private final DecimalFormat decimalFormat = new DecimalFormat("#.#");
            private String stepTxt = "";
            private String percentTxt = "0.0%";

            @Override
            public void step(Step step) {
                Platform.runLater(() -> {
                    stepTxt = StepInfo.valueOf(step.name()).getDetails();
                    setStatus(String.format("%s (%s)", stepTxt, percentTxt));

                });
            }


            public void update(DownloadList.DownloadInfo info) {
                double progress = info.getDownloadedBytes() / info.getTotalToDownloadBytes();
                Platform.runLater(() -> {
                    percentTxt = decimalFormat.format(info.getDownloadedFiles() * 100.d / info.getTotalToDownloadFiles()) + "%";
                    setStatus(String.format("%s (%s)", stepTxt, percentTxt));
                    setProgress(info.getDownloadedFiles(), info.getTotalToDownloadFiles());
                });
            }

            @Override
            public void onFileDownloaded(Path path) {
                Platform.runLater(() -> {
                    String p = path.toString();
                    fileLabel.setText("..." + p.replace(Launcher.getInstance().getLauncherDir().toFile().getAbsolutePath(), ""));
                });
            }

        };

        try {


        final VanillaVersion version = new VanillaVersion.VanillaVersionBuilder()
                .withName(MinecraftInfos.GAME_VERSION)
                .build();

        final  List<CurseFileInfo> modInfos = new ArrayList<>();

        modInfos.add(new CurseFileInfo( 377448,  4428616));
            modInfos.add(new CurseFileInfo( 228756,  3966365));
            modInfos.add(new CurseFileInfo( 268560,  4385637));

            modInfos.add(new CurseFileInfo( 634062,  4082456));
            modInfos.add(new CurseFileInfo( 223852,  3884263));



            final UpdaterOptions options = new UpdaterOptions.UpdaterOptionsBuilder()
                .build();


        final AbstractForgeVersion forge = new ForgeVersionBuilder(MinecraftInfos.FORGE_VERSION_TYPE)
                .withForgeVersion(MinecraftInfos.FORGE_VERSION)
                .withCurseMods(modInfos)
                //.withCurseMods(CurseFileInfo.getFilesFromJson("https://odysseyus.fr/cursefiles.json"))
                //.withOptiFine(new OptiFineInfo(MinecraftInfos.OPTIFINE_VERSION, false))
                .withFileDeleter(new ModFileDeleter(false))
                //.withCurseModPack(new CurseModPackInfo( 690756, 4411312, true))
                .build();

        final FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(version)
                .withLogger(Launcher.getInstance().getLogger())
                .withProgressCallback(callback)
                .withModLoaderVersion(forge)
                .withUpdaterOptions(options)
                //.withPostExecutions(Collections.singletonList(postExecutions))
                .build();

        updater.update(Launcher.getInstance().getLauncherDir());
        this.startGame(MinecraftInfos.GAME_VERSION);


    } catch (Exception exception) {
        Launcher.getInstance().getLogger().printStackTrace(exception);
        Platform.runLater(() -> panelManager.getStage().show());
    }

}

    public void startGame(String gameVersion) {
        GameInfos infos = new GameInfos(
                MinecraftInfos.SERVER_NAME,
                true,
                new GameVersion(gameVersion, MinecraftInfos.OLL_GAME_TYPE),
                new GameTweak[]{GameTweak.FORGE}
        );

        try {
            final NoFramework noFramework = new NoFramework(Launcher.getInstance().getLauncherDir(), Launcher.getInstance().getAuthInfos(), GameFolder.FLOW_UPDATER);
            noFramework.getAdditionalVmArgs().add(this.getRamArgsFromSaver());
            //noFramework.getAdditionalArgs().addAll(Arrays.asList("--server=panel.odysseyus.fr", "--port=25569"));
            Process p = noFramework.launch(MinecraftInfos.GAME_VERSION, MinecraftInfos.FORGE_VERSION.split("-")[1], NoFramework.ModLoader.FORGE);

            Platform.runLater(() -> panelManager.getStage().hide());


            p.waitFor();
            Platform.runLater(Platform::exit);
        } catch (Exception exception) {
            exception.printStackTrace();
            Launcher.getInstance().getLogger().err(exception.toString());
        }



    }

    public String getRamArgsFromSaver() {
        int val = 1024;
        try {
            if (saver.get("maxRam") != null) {
                val = Integer.parseInt(saver.get("maxRam"));
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException error) {
            saver.set("maxRam", String.valueOf(val));
            saver.save();
        }

        return "-Xmx" + val + "M";
    }

    public void setStatus(String status) {
        this.stepLabel.setText(status);
    }

    public void setProgress(double current, double max) {
        this.progressBar.setProgress(current / max);
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public enum StepInfo {
        READ("Lecture du fichier json..."),
        DL_LIBS("Téléchargement des libraries..."),
        DL_ASSETS("Téléchargement des ressources..."),
        EXTRACT_NATIVES("Extraction des natives..."),
        FORGE("Installation de forge..."),
        FABRIC("Installation de fabric..."),
        MODS("Téléchargement des mods..."),
        EXTERNAL_FILES("Téléchargement des fichier externes..."),
        POST_EXECUTIONS("Exécution post-installation..."),
        END("Finit !");
        String details;

        StepInfo(String details) {
            this.details = details;
        }

        public String getDetails() {
            return details;
        }
    }
}

package no.velocitymedia.velocitymedia_backend.service.generators;

import java.io.File;
import java.io.IOException;

public class ThumbnailGenerator {

    public static void generateThumbnail(String videoPath, String thumbnailPath) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "C:\\Users\\danir\\AppData\\Local\\Microsoft\\WinGet\\Packages\\Gyan.FFmpeg_Microsoft.Winget.Source_8wekyb3d8bbwe\\ffmpeg-7.1.1-full_build\\bin\\ffmpeg.exe",
            "-i", videoPath,
            "-ss", "00:00:01.000",
            "-vframes", "1",
            thumbnailPath
        );

        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        processBuilder.directory(new File("."));

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg process failed with code " + exitCode);
        }
    }
}
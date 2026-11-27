package br.ufrrj.sgha.config;

import java.io.File;

public class AppConfig {
    // Pasta fora do ambiente do container web para armazenar os comprovantes
    public static final String UPLOAD_DIR = "C:/sgha-uploads/";

    public static File garantirDiretorioUpload() {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
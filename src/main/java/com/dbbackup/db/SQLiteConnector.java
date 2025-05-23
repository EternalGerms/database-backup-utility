package com.dbbackup.db;

import java.io.File;
import java.util.zip.GZIPOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SQLiteConnector implements DatabaseConnector {
    private String caminhoArquivoBanco;

    public SQLiteConnector(String caminhoArquivoBanco) {
        this.caminhoArquivoBanco = caminhoArquivoBanco;
    }

    @Override
    public boolean testarConexao() {
        String url = "jdbc:sqlite:" + caminhoArquivoBanco;
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url)) {
            System.out.println("Conexão com SQLite bem-sucedida!");
            return true;
        } catch (Exception e) {
            System.out.println("Falha na conexão com SQLite: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean realizarBackup(File destino, String tipoBackup, boolean comprimir) throws Exception {
        if (tipoBackup != null && !tipoBackup.equalsIgnoreCase("completo")) {
            throw new UnsupportedOperationException("Backup '" + tipoBackup + "' não suportado para SQLite. Apenas backup completo é suportado.");
        }
        java.nio.file.Path origem = java.nio.file.Paths.get(caminhoArquivoBanco);
        java.nio.file.Path destinoPath = destino.toPath();
        try {
            java.nio.file.Files.copy(origem, destinoPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            if (comprimir) {
                File gzFile = new File(destino.getAbsolutePath() + ".gz");
                try (FileInputStream fis = new FileInputStream(destino);
                     FileOutputStream fos = new FileOutputStream(gzFile);
                     GZIPOutputStream gzos = new GZIPOutputStream(fos)) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        gzos.write(buffer, 0, len);
                    }
                }
                destino.delete();
                System.out.println("Backup do SQLite compactado em: " + gzFile.getAbsolutePath());
            }
            System.out.println("Backup do SQLite realizado com sucesso!");
            return true;
        } catch (Exception e) {
            System.out.println("Falha ao realizar backup do SQLite: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean restaurarBackup(File arquivoBackup) throws Exception {
        java.nio.file.Path destino = java.nio.file.Paths.get(caminhoArquivoBanco);
        java.nio.file.Path origem = arquivoBackup.toPath();
        try {
            java.nio.file.Files.copy(origem, destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Restauração do SQLite realizada com sucesso!");
            return true;
        } catch (Exception e) {
            System.out.println("Falha ao restaurar backup do SQLite: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getTipoBanco() {
        return "SQLite";
    }
} 
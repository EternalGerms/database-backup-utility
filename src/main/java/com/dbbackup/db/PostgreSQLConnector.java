package com.dbbackup.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.zip.GZIPOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class PostgreSQLConnector implements DatabaseConnector {
    private String host;
    private int porta;
    private String usuario;
    private String senha;
    private String nomeBanco;

    public PostgreSQLConnector(String host, int porta, String usuario, String senha, String nomeBanco) {
        this.host = host;
        this.porta = porta;
        this.usuario = usuario;
        this.senha = senha;
        this.nomeBanco = nomeBanco;
    }

    @Override
    public boolean testarConexao() {
        String url = String.format("jdbc:postgresql://%s:%d/%s", host, porta, nomeBanco);
        try (Connection conn = DriverManager.getConnection(url, usuario, senha)) {
            System.out.println("Conexão com PostgreSQL bem-sucedida!");
            return true;
        } catch (Exception e) {
            System.out.println("Falha na conexão com PostgreSQL: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean realizarBackup(File destino, String tipoBackup, boolean comprimir) throws Exception {
        if (tipoBackup != null && !tipoBackup.equalsIgnoreCase("completo")) {
            throw new UnsupportedOperationException("Backup '" + tipoBackup + "' não suportado para PostgreSQL. Apenas backup completo é suportado.");
        }
        // Backup usando pg_dump
        String[] command = {
            "pg_dump",
            "-h", host,
            "-p", String.valueOf(porta),
            "-U", usuario,
            "-F", "c", // formato custom
            "-f", destino.getAbsolutePath(),
            nomeBanco
        };
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.environment().put("PGPASSWORD", senha);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode == 0) {
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
                System.out.println("Backup do PostgreSQL compactado em: " + gzFile.getAbsolutePath());
            }
            System.out.println("Backup do PostgreSQL realizado com sucesso!");
            return true;
        } else {
            System.out.println("Falha ao realizar backup do PostgreSQL. Código de saída: " + exitCode);
            return false;
        }
    }

    @Override
    public boolean restaurarBackup(File arquivoBackup) throws Exception {
        // Restauração usando psql
        String[] command = {
            "pg_restore",
            "-h", host,
            "-p", String.valueOf(porta),
            "-U", usuario,
            "-d", nomeBanco,
            arquivoBackup.getAbsolutePath()
        };
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.environment().put("PGPASSWORD", senha);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Restauração do PostgreSQL realizada com sucesso!");
            return true;
        } else {
            System.out.println("Falha ao restaurar backup do PostgreSQL. Código de saída: " + exitCode);
            return false;
        }
    }

    @Override
    public String getTipoBanco() {
        return "PostgreSQL";
    }
} 
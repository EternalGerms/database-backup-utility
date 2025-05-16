package com.dbbackup.db;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLConnector implements DatabaseConnector {
    private String host;
    private int porta;
    private String usuario;
    private String senha;
    private String nomeBanco;

    public MySQLConnector(String host, int porta, String usuario, String senha, String nomeBanco) {
        this.host = host;
        this.porta = porta;
        this.usuario = usuario;
        this.senha = senha;
        this.nomeBanco = nomeBanco;
    }

    @Override
    public boolean testarConexao() {
        String url = "jdbc:mysql://" + host + ":" + porta + "/" + nomeBanco;
        try (Connection conn = DriverManager.getConnection(url, usuario, senha)) {
            System.out.println("Conexão com MySQL bem-sucedida!");
            return true;
        } catch (Exception e) {
            System.out.println("Falha na conexão com MySQL: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean realizarBackup(File destino, String tipoBackup) throws Exception {
        String[] command = {
            "mysqldump",
            "-h" + host,
            "-P" + porta,
            "-u" + usuario,
            "-p" + senha,
            nomeBanco
        };
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();

        // Redireciona a saída do processo para o arquivo de destino
        try (java.io.InputStream is = process.getInputStream();
             java.io.FileOutputStream fos = new java.io.FileOutputStream(destino)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Backup do MySQL realizado com sucesso!");
            return true;
        } else {
            System.out.println("Falha ao realizar backup do MySQL. Código de saída: " + exitCode);
            return false;
        }
    }

    @Override
    public boolean restaurarBackup(File arquivoBackup) throws Exception {
        String[] command = {
            "mysql",
            "-h", host,
            "-P", String.valueOf(porta),
            "-u", usuario,
            "-p" + senha,
            nomeBanco
        };
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        pb.redirectInput(arquivoBackup);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Restauração do MySQL realizada com sucesso!");
            return true;
        } else {
            System.out.println("Falha ao restaurar backup do MySQL. Código de saída: " + exitCode);
            return false;
        }
    }

    @Override
    public String getTipoBanco() {
        return "MySQL";
    }
} 
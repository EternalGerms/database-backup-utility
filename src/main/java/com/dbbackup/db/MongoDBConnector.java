package com.dbbackup.db;

import java.io.File;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.ArrayList;
import java.util.List;

public class MongoDBConnector implements DatabaseConnector {
    private String host;
    private int porta;
    private String usuario;
    private String senha;
    private String nomeBanco;

    public MongoDBConnector(String host, int porta, String usuario, String senha, String nomeBanco) {
        this.host = host;
        this.porta = porta;
        this.usuario = usuario;
        this.senha = senha;
        this.nomeBanco = nomeBanco;
    }

    @Override
    public boolean testarConexao() {
        String uri = String.format("mongodb://%s:%s@%s:%d/%s", usuario, senha, host, porta, nomeBanco);
        try (MongoClient client = MongoClients.create(uri)) {
            client.getDatabase(nomeBanco).listCollectionNames().first();
            System.out.println("Conexão com MongoDB bem-sucedida!");
            return true;
        } catch (Exception e) {
            System.out.println("Falha na conexão com MongoDB: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean realizarBackup(File destino, String tipoBackup) throws Exception {
        // Backup usando mongodump
        String dumpDir = destino.getParent();
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;
        if (os.contains("win")) {
            List<String> command = new ArrayList<>();
            command.add("mongodump");
            command.add("--host"); command.add(host);
            command.add("--port"); command.add(String.valueOf(porta));
            if (usuario != null && !usuario.isBlank()) {
                command.add("--username"); command.add(usuario);
            }
            if (senha != null && !senha.isBlank()) {
                command.add("--password"); command.add(senha);
            }
            command.add("--db"); command.add(nomeBanco);
            command.add("--out"); command.add(dumpDir);
            pb = new ProcessBuilder(command);
        } else {
            StringBuilder dumpCommand = new StringBuilder();
            dumpCommand.append(String.format("mongodump --host %s --port %d ", host, porta));
            if (usuario != null && !usuario.isBlank()) {
                dumpCommand.append(String.format("--username %s ", usuario));
            }
            if (senha != null && !senha.isBlank()) {
                dumpCommand.append(String.format("--password %s ", senha));
            }
            dumpCommand.append(String.format("--db %s --out %s", nomeBanco, dumpDir));
            pb = new ProcessBuilder("bash", "-c", dumpCommand.toString());
        }
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Backup do MongoDB realizado com sucesso!");
            return true;
        } else {
            System.out.println("Falha ao realizar backup do MongoDB. Código de saída: " + exitCode);
            return false;
        }
    }

    @Override
    public boolean restaurarBackup(File arquivoBackup) throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;
        if (os.contains("win")) {
            List<String> command = new ArrayList<>();
            command.add("mongorestore");
            command.add("--host"); command.add(host);
            command.add("--port"); command.add(String.valueOf(porta));
            if (usuario != null && !usuario.isBlank()) {
                command.add("--username"); command.add(usuario);
            }
            if (senha != null && !senha.isBlank()) {
                command.add("--password"); command.add(senha);
            }
            command.add("--db"); command.add(nomeBanco);
            command.add(arquivoBackup.getAbsolutePath());
            pb = new ProcessBuilder(command);
        } else {
            StringBuilder restoreCommand = new StringBuilder();
            restoreCommand.append(String.format("mongorestore --host %s --port %d ", host, porta));
            if (usuario != null && !usuario.isBlank()) {
                restoreCommand.append(String.format("--username %s ", usuario));
            }
            if (senha != null && !senha.isBlank()) {
                restoreCommand.append(String.format("--password %s ", senha));
            }
            restoreCommand.append(String.format("--db %s %s", nomeBanco, arquivoBackup.getAbsolutePath()));
            pb = new ProcessBuilder("bash", "-c", restoreCommand.toString());
        }
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Restauração do MongoDB realizada com sucesso!");
            return true;
        } else {
            System.out.println("Falha ao restaurar backup do MongoDB. Código de saída: " + exitCode);
            return false;
        }
    }

    @Override
    public String getTipoBanco() {
        return "MongoDB";
    }
} 
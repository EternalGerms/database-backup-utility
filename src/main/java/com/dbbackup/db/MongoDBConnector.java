package com.dbbackup.db;

import java.io.File;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

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
        String dumpCommand = String.format(
            "mongodump --host %s --port %d --username %s --password %s --db %s --out %s",
            host, porta, usuario, senha, nomeBanco, dumpDir
        );
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("bash", "-c", dumpCommand);
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
        // Restauração usando mongorestore
        String restoreCommand = String.format(
            "mongorestore --host %s --port %d --username %s --password %s --db %s %s",
            host, porta, usuario, senha, nomeBanco, arquivoBackup.getAbsolutePath()
        );
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("bash", "-c", restoreCommand);
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
package com.dbbackup.db;

import java.io.File;

public class SQLiteConnector implements DatabaseConnector {
    private String caminhoArquivoBanco;

    public SQLiteConnector(String caminhoArquivoBanco) {
        this.caminhoArquivoBanco = caminhoArquivoBanco;
    }

    @Override
    public boolean testarConexao() {
        System.out.println("Arquivo do banco: " + caminhoArquivoBanco);
        // TODO: Implementar teste real de conexão com SQLite
        return false;
    }

    @Override
    public boolean realizarBackup(File destino, String tipoBackup) throws Exception {
        // TODO: Implementar lógica de backup (cópia do arquivo, etc.)
        return false;
    }

    @Override
    public boolean restaurarBackup(File arquivoBackup) throws Exception {
        // TODO: Implementar lógica de restauração (substituir arquivo, etc.)
        return false;
    }

    @Override
    public String getTipoBanco() {
        return "SQLite";
    }
} 
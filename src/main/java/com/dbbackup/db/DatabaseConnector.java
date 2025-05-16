package com.dbbackup.db;

import java.io.File;

public interface DatabaseConnector {
    boolean testarConexao();
    boolean realizarBackup(File destino, String tipoBackup) throws Exception;
    boolean restaurarBackup(File arquivoBackup) throws Exception;
    String getTipoBanco();
} 
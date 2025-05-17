package com.dbbackup.db;

import java.io.File;

public interface DatabaseConnector {
    boolean testarConexao();
    /**
     * Realiza o backup do banco de dados.
     * @param destino Arquivo ou diretório de destino do backup
     * @param tipoBackup Tipo de backup: 'completo', 'incremental' ou 'diferencial'.
     *                   A implementação depende do SGBD.
     * @param comprimir Se true, o backup será compactado (gzip ou zip).
     */
    boolean realizarBackup(File destino, String tipoBackup, boolean comprimir) throws Exception;
    boolean restaurarBackup(File arquivoBackup) throws Exception;
    String getTipoBanco();
} 
package com.dbbackup.backup;

import java.io.File;

public interface BackupService {
    boolean executarBackup(String tipoBanco, String host, int porta, String usuario, String senha, String nomeBanco, File destino, String tipoBackup) throws Exception;
    boolean restaurar(String tipoBanco, File arquivoBackup, String host, int porta, String usuario, String senha, String nomeBanco) throws Exception;
} 
package com.dbbackup.backup;

import com.dbbackup.db.DatabaseConnector;
import com.dbbackup.db.MySQLConnector;
import com.dbbackup.db.PostgreSQLConnector;
import com.dbbackup.db.MongoDBConnector;
import com.dbbackup.db.SQLiteConnector;
import java.io.File;

public class BackupServiceImpl implements BackupService {
    @Override
    public boolean executarBackup(String tipoBanco, String host, int porta, String usuario, String senha, String nomeBanco, File destino, String tipoBackup) throws Exception {
        DatabaseConnector connector = getConnector(tipoBanco, host, porta, usuario, senha, nomeBanco);
        return connector.realizarBackup(destino, tipoBackup);
    }

    @Override
    public boolean restaurar(String tipoBanco, File arquivoBackup, String host, int porta, String usuario, String senha, String nomeBanco) throws Exception {
        DatabaseConnector connector = getConnector(tipoBanco, host, porta, usuario, senha, nomeBanco);
        return connector.restaurarBackup(arquivoBackup);
    }

    private DatabaseConnector getConnector(String tipoBanco, String host, int porta, String usuario, String senha, String nomeBanco) {
        if ("mysql".equalsIgnoreCase(tipoBanco)) {
            return new MySQLConnector(host, porta, usuario, senha, nomeBanco);
        } else if ("postgresql".equalsIgnoreCase(tipoBanco)) {
            return new PostgreSQLConnector(host, porta, usuario, senha, nomeBanco);
        } else if ("mongodb".equalsIgnoreCase(tipoBanco)) {
            return new MongoDBConnector(host, porta, usuario, senha, nomeBanco);
        } else if ("sqlite".equalsIgnoreCase(tipoBanco)) {
            // Para SQLite, o parâmetro nomeBanco pode ser usado como caminho do arquivo
            return new SQLiteConnector(nomeBanco);
        }
        throw new UnsupportedOperationException("Tipo de banco não suportado: " + tipoBanco);
    }
} 
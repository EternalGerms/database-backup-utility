package com.dbbackup.cli;

import com.dbbackup.backup.BackupService;
import com.dbbackup.backup.BackupServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;

@Command(name = "dbbackup", mixinStandardHelpOptions = true, version = "1.0",
        description = "Utilitário de Backup de Banco de Dados: backup e restauração para múltiplos SGBDs.")
public class DatabaseBackupCLI implements Runnable {

    @Option(names = {"-b", "--backup"}, description = "Executa uma operação de backup.")
    boolean backup;

    @Option(names = {"-r", "--restore"}, description = "Executa uma operação de restauração.")
    boolean restore;

    @Option(names = {"--banco"}, description = "Tipo do banco de dados (ex: mysql)")
    String tipoBanco;

    @Option(names = {"--host"}, description = "Host do banco de dados")
    String host = "localhost";

    @Option(names = {"--porta"}, description = "Porta do banco de dados")
    int porta = 3306;

    @Option(names = {"--usuario"}, description = "Usuário do banco de dados")
    String usuario;

    @Option(names = {"--senha"}, description = "Senha do banco de dados")
    String senha;

    @Option(names = {"--nome-banco"}, description = "Nome do banco de dados")
    String nomeBanco;

    @Option(names = {"--tipo-backup"}, description = "Tipo de backup (completo, incremental, diferencial)")
    String tipoBackup = "completo";

    @Option(names = {"--arquivo"}, description = "Caminho do arquivo de backup/restauração")
    String caminhoArquivo;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new DatabaseBackupCLI()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        BackupService backupService = new BackupServiceImpl();
        try {
            if (backup) {
                if (parametrosValidosBackup()) {
                    System.out.println("tipoBanco: " + tipoBanco);
                    System.out.println("nomeBanco: " + nomeBanco);
                    System.out.println("caminhoArquivo: " + caminhoArquivo);
                    boolean sucesso = backupService.executarBackup(
                        tipoBanco, host, porta, usuario, senha, nomeBanco,
                        new File(caminhoArquivo), tipoBackup
                    );
                    System.out.println(sucesso ? "Backup realizado com sucesso!" : "Falha ao realizar backup.");
                } else {
                    System.out.println("Parâmetros obrigatórios para backup não informados. Use --help para detalhes.");
                }
            } else if (restore) {
                if (parametrosValidosRestore()) {
                    System.out.println("tipoBanco: " + tipoBanco);
                    System.out.println("nomeBanco: " + nomeBanco);
                    System.out.println("caminhoArquivo: " + caminhoArquivo);
                    boolean sucesso = backupService.restaurar(
                        tipoBanco, new File(caminhoArquivo), host, porta, usuario, senha, nomeBanco
                    );
                    System.out.println(sucesso ? "Restauração realizada com sucesso!" : "Falha ao restaurar backup.");
                } else {
                    System.out.println("Parâmetros obrigatórios para restauração não informados. Use --help para detalhes.");
                }
            } else {
                System.out.println("Nenhuma operação especificada. Use --help para instruções de uso.");
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean parametrosValidosBackup() {
        if ("sqlite".equalsIgnoreCase(tipoBanco)) {
            return nomeBanco != null && !nomeBanco.isBlank() && caminhoArquivo != null && !caminhoArquivo.isBlank();
        }
        if ("mongodb".equalsIgnoreCase(tipoBanco)) {
            return tipoBanco != null && !tipoBanco.isBlank()
                && nomeBanco != null && !nomeBanco.isBlank()
                && caminhoArquivo != null && !caminhoArquivo.isBlank();
        }
        return tipoBanco != null && !tipoBanco.isBlank()
            && usuario != null && !usuario.isBlank()
            && senha != null && !senha.isBlank()
            && nomeBanco != null && !nomeBanco.isBlank()
            && caminhoArquivo != null && !caminhoArquivo.isBlank();
    }

    private boolean parametrosValidosRestore() {
        if ("sqlite".equalsIgnoreCase(tipoBanco)) {
            return nomeBanco != null && !nomeBanco.isBlank() && caminhoArquivo != null && !caminhoArquivo.isBlank();
        }
        if ("mongodb".equalsIgnoreCase(tipoBanco)) {
            return tipoBanco != null && !tipoBanco.isBlank()
                && nomeBanco != null && !nomeBanco.isBlank()
                && caminhoArquivo != null && !caminhoArquivo.isBlank();
        }
        return tipoBanco != null && !tipoBanco.isBlank()
            && usuario != null && !usuario.isBlank()
            && senha != null && !senha.isBlank()
            && nomeBanco != null && !nomeBanco.isBlank()
            && caminhoArquivo != null && !caminhoArquivo.isBlank();
    }
} 
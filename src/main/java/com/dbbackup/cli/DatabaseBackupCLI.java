package com.dbbackup.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "dbbackup", mixinStandardHelpOptions = true, version = "1.0",
        description = "Utilitário de Backup de Banco de Dados: backup e restauração para múltiplos SGBDs.")
public class DatabaseBackupCLI implements Runnable {

    @Option(names = {"-b", "--backup"}, description = "Executa uma operação de backup.")
    boolean backup;

    @Option(names = {"-r", "--restore"}, description = "Executa uma operação de restauração.")
    boolean restore;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new DatabaseBackupCLI()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        if (backup) {
            System.out.println("Operação de backup selecionada. (Implementação pendente)");
        } else if (restore) {
            System.out.println("Operação de restauração selecionada. (Implementação pendente)");
        } else {
            System.out.println("Nenhuma operação especificada. Use --help para instruções de uso.");
        }
    }
} 
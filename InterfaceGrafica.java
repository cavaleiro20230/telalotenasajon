// Demonstração da interface gráfica do sistema de lotes usando JavaFX

console.log("Interface Gráfica do Sistema de Envio de Lote em Java para Nasajon\n");

// Estrutura do projeto com arquivos da GUI
const projetoComGui = `
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── contabilidade/
│   │           └── nasajon/
│   │               ├── Application.java
│   │               ├── config/
│   │               │   ├── DatabaseConfig.java
│   │               │   └── NasajonApiConfig.java
│   │               ├── model/
│   │               │   ├── ContabilidadeLote.java
│   │               │   ├── LancamentoContabil.java
│   │               │   └── RespostaEnvio.java
│   │               ├── service/
│   │               │   ├── ContabilidadeService.java
│   │               │   ├── LoteService.java
│   │               │   └── NasajonApiService.java
│   │               ├── repository/
│   │               │   └── LoteRepository.java
│   │               ├── exception/
│   │               │   ├── ApiException.java
│   │               │   └── ProcessamentoException.java
│   │               ├── util/
│   │               │   ├── DataConverter.java
│   │               │   └── LogUtil.java
│   │               ├── gui/
│   │               │   ├── MainApplication.java
│   │               │   ├── controller/
│   │               │   │   ├── DashboardController.java
│   │               │   │   ├── LoteListController.java
│   │               │   │   ├── ConfiguracaoController.java
│   │               │   │   └── LogViewController.java
│   │               │   ├── view/
│   │               │   │   ├── Dashboard.fxml
│   │               │   │   ├── LoteList.fxml
│   │               │   │   ├── Configuracao.fxml
│   │               │   │   └── LogView.fxml
│   │               │   └── util/
│   │               │       ├── AlertUtil.java
│   │               │       └── UIRefreshService.java
│   │               └── task/
│   │                   ├── ProcessarLoteTask.java
│   │                   └── GerarLoteTask.java
│   └── resources/
│       ├── application.properties
│       ├── logback.xml
│       ├── css/
│       │   └── application.css
│       └── images/
│           ├── logo.png
│           └── icons/
│               ├── refresh.png
│               ├── send.png
│               └── settings.png
└── test/
    └── java/
        └── com/
            └── contabilidade/
                └── nasajon/
                    └── ...
`;

console.log("Estrutura do projeto com GUI:");
console.log(projetoComGui);

// Classe principal da aplicação JavaFX
const mainApplicationJava = `
package com.contabilidade.nasajon.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class MainApplication extends Application {

    private ConfigurableApplicationContext springContext;
    
    @Override
    public void init() {
        // Inicializa o contexto Spring
        springContext = new SpringApplicationBuilder(com.contabilidade.nasajon.Application.class)
            .headless(false)
            .run();
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega o FXML usando o contexto Spring para injeção de dependências
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
        loader.setControllerFactory(springContext::getBean);
        
        Parent root = loader.load();
        
        // Configura a cena principal
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
        
        // Configura o estágio principal
        primaryStage.setTitle("Sistema de Envio de Lotes - Contabilidade para Nasajon");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
    @Override
    public void stop() {
        // Fecha o contexto Spring ao fechar a aplicação
        springContext.close();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
`;

console.log("\nMainApplication.java (Classe principal da GUI):");
console.log(mainApplicationJava);

// Controlador do Dashboard
const dashboardControllerJava = `
package com.contabilidade.nasajon.gui.controller;

import com.contabilidade.nasajon.model.ContabilidadeLote;
import com.contabilidade.nasajon.service.LoteService;
import com.contabilidade.nasajon.gui.util.AlertUtil;
import com.contabilidade.nasajon.task.GerarLoteTask;
import com.contabilidade.nasajon.task.ProcessarLoteTask;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DashboardController implements Initializable {

    @Autowired
    private LoteService loteService;
    
    @FXML
    private BorderPane mainPane;
    
    @FXML
    private PieChart statusChart;
    
    @FXML
    private Label totalLotesLabel;
    
    @FXML
    private Label lotesEnviadosLabel;
    
    @FXML
    private Label lotesErroLabel;
    
    @FXML
    private Label lotesPendentesLabel;
    
    @FXML
    private TableView<ContabilidadeLote> lotesTable;
    
    @FXML
    private TableColumn<ContabilidadeLote, String> codigoLoteColumn;
    
    @FXML
    private TableColumn<ContabilidadeLote, String> statusColumn;
    
    @FXML
    private TableColumn<ContabilidadeLote, String> dataGeracaoColumn;
    
    @FXML
    private TableColumn<ContabilidadeLote, String> dataEnvioColumn;
    
    private ScheduledExecutorService refreshService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configura as colunas da tabela
        configurarTabela();
        
        // Carrega os dados iniciais
        carregarDados();
        
        // Configura atualização automática a cada 30 segundos
        refreshService = Executors.newSingleThreadScheduledExecutor();
        refreshService.scheduleAtFixedRate(this::atualizarDadosAsync, 30, 30, TimeUnit.SECONDS);
    }
    
    private void configurarTabela() {
        // Configuração das colunas da tabela
        codigoLoteColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCodigoLote()));
            
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
            
        dataGeracaoColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDataGeracao() != null ? 
                cellData.getValue().getDataGeracao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""));
                
        dataEnvioColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDataEnvio() != null ? 
                cellData.getValue().getDataEnvio().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""));
                
        // Adiciona menu de contexto para ações na tabela
        lotesTable.setRowFactory(tv -> {
            TableRow<ContabilidadeLote> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            
            MenuItem enviarItem = new MenuItem("Enviar Lote");
            enviarItem.setOnAction(event -> enviarLoteSelecionado());
            
            MenuItem detalhesItem = new MenuItem("Ver Detalhes");
            detalhesItem.setOnAction(event -> verDetalhesDeLote());
            
            MenuItem reprocessarItem = new MenuItem("Reprocessar");
            reprocessarItem.setOnAction(event -> reprocessarLoteSelecionado());
            
            contextMenu.getItems().addAll(enviarItem, detalhesItem, reprocessarItem);
            
            // Só mostra o menu de contexto para linhas não vazias
            row.contextMenuProperty().bind(
                javafx.beans.binding.Bindings.when(row.emptyProperty())
                .then((ContextMenu)null)
                .otherwise(contextMenu)
            );
            
            return row;
        });
    }
    
    private void carregarDados() {
        try {
            // Busca todos os lotes
            List<ContabilidadeLote> lotes = loteService.buscarTodosLotes();
            
            // Atualiza a tabela
            lotesTable.setItems(FXCollections.observableArrayList(lotes));
            
            // Atualiza estatísticas
            atualizarEstatisticas(lotes);
            
        } catch (Exception e) {
            AlertUtil.showError("Erro ao carregar dados", e.getMessage());
        }
    }
    
    private void atualizarEstatisticas(List<ContabilidadeLote> lotes) {
        // Contadores por status
        Map<String, Integer> contagemPorStatus = new HashMap<>();
        contagemPorStatus.put("PENDENTE", 0);
        contagemPorStatus.put("PROCESSANDO", 0);
        contagemPorStatus.put("ENVIADO", 0);
        contagemPorStatus.put("ERRO", 0);
        
        // Conta lotes por status
        for (ContabilidadeLote lote : lotes) {
            String status = lote.getStatus();
            contagemPorStatus.put(status, contagemPorStatus.getOrDefault(status, 0) + 1);
        }
        
        // Atualiza labels
        totalLotesLabel.setText(String.valueOf(lotes.size()));
        lotesPendentesLabel.setText(String.valueOf(contagemPorStatus.get("PENDENTE") + contagemPorStatus.get("PROCESSANDO")));
        lotesEnviadosLabel.setText(String.valueOf(contagemPorStatus.get("ENVIADO")));
        lotesErroLabel.setText(String.valueOf(contagemPorStatus.get("ERRO")));
        
        // Atualiza gráfico
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        pieChartData.add(new PieChart.Data("Pendentes", contagemPorStatus.get("PENDENTE")));
        pieChartData.add(new PieChart.Data("Processando", contagemPorStatus.get("PROCESSANDO")));
        pieChartData.add(new PieChart.Data("Enviados", contagemPorStatus.get("ENVIADO")));
        pieChartData.add(new PieChart.Data("Erro", contagemPorStatus.get("ERRO")));
        
        statusChart.setData(pieChartData);
    }
    
    private void atualizarDadosAsync() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> carregarDados());
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    @FXML
    private void gerarNovoLote() {
        try {
            // Cria uma tarefa para gerar o lote em segundo plano
            GerarLoteTask task = new GerarLoteTask(loteService);
            
            // Mostra diálogo de progresso
            ProgressDialog progressDialog = new ProgressDialog(task, "Gerando novo lote...");
            progressDialog.showAndWait();
            
            // Atualiza a interface após conclusão
            carregarDados();
            
            // Mostra mensagem de sucesso
            AlertUtil.showInfo("Lote Gerado", "Novo lote gerado com sucesso!");
            
        } catch (Exception e) {
            AlertUtil.showError("Erro ao gerar lote", e.getMessage());
        }
    }
    
    @FXML
    private void processarLotesPendentes() {
        try {
            // Cria uma tarefa para processar os lotes em segundo plano
            ProcessarLoteTask task = new ProcessarLoteTask(loteService);
            
            // Mostra diálogo de progresso
            ProgressDialog progressDialog = new ProgressDialog(task, "Processando lotes pendentes...");
            progressDialog.showAndWait();
            
            // Atualiza a interface após conclusão
            carregarDados();
            
            // Mostra mensagem de sucesso
            AlertUtil.showInfo("Processamento Concluído", "Lotes processados com sucesso!");
            
        } catch (Exception e) {
            AlertUtil.showError("Erro ao processar lotes", e.getMessage());
        }
    }
    
    private void enviarLoteSelecionado() {
        ContabilidadeLote loteSelecionado = lotesTable.getSelectionModel().getSelectedItem();
        
        if (loteSelecionado == null) {
            AlertUtil.showWarning("Seleção Necessária", "Selecione um lote para enviar.");
            return;
        }
        
        try {
            // Envia o lote selecionado
            loteService.enviarLote(loteSelecionado);
            
            // Atualiza a interface
            carregarDados();
            
            // Mostra mensagem de sucesso
            AlertUtil.showInfo("Lote Enviado", "Lote enviado com sucesso!");
            
        } catch (Exception e) {
            AlertUtil.showError("Erro ao enviar lote", e.getMessage());
        }
    }
    
    private void reprocessarLoteSelecionado() {
        ContabilidadeLote loteSelecionado = lotesTable.getSelectionModel().getSelectedItem();
        
        if (loteSelecionado == null) {
            AlertUtil.showWarning("Seleção Necessária", "Selecione um lote para reprocessar.");
            return;
        }
        
        if (!"ERRO".equals(loteSelecionado.getStatus())) {
            AlertUtil.showWarning("Operação Inválida", "Apenas lotes com erro podem ser reprocessados.");
            return;
        }
        
        try {
            // Marca o lote para reprocessamento
            loteSelecionado.setStatus("PENDENTE");
            loteSelecionado.setMensagemErro(null);
            loteService.salvarLote(loteSelecionado);
            
            // Atualiza a interface
            carregarDados();
            
            // Mostra mensagem de sucesso
            AlertUtil.showInfo("Lote Marcado", "Lote marcado para reprocessamento!");
            
        } catch (Exception e) {
            AlertUtil.showError("Erro ao reprocessar lote", e.getMessage());
        }
    }
    
    private void verDetalhesDeLote() {
        ContabilidadeLote loteSelecionado = lotesTable.getSelectionModel().getSelectedItem();
        
        if (loteSelecionado == null) {
            AlertUtil.showWarning("Seleção Necessária", "Selecione um lote para ver detalhes.");
            return;
        }
        
        try {
            // Carrega a tela de detalhes
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoteDetails.fxml"));
            Parent root = loader.load();
            
            // Configura o controlador
            LoteDetailsController controller = loader.getController();
            controller.setLote(loteSelecionado);
            
            // Mostra a janela de detalhes
            Stage stage = new Stage();
            stage.setTitle("Detalhes do Lote: " + loteSelecionado.getCodigoLote());
            stage.setScene(new Scene(root, 800, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (Exception e) {
            AlertUtil.showError("Erro ao abrir detalhes", e.getMessage());
        }
    }
    
    @FXML
    private void abrirConfiguracoes() {
        try {
            // Carrega a tela de configurações
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Configuracao.fxml"));
            Parent root = loader.load();
            
            // Mostra a janela de configurações
            Stage stage = new Stage();
            stage.setTitle("Configurações do Sistema");
            stage.setScene(new Scene(root, 600, 400));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (Exception e) {
            AlertUtil.showError("Erro ao abrir configurações", e.getMessage());
        }
    }
    
    @FXML
    private void abrirLogs() {
        try {
            // Carrega a tela de logs
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogView.fxml"));
            Parent root = loader.load();
            
            // Mostra a janela de logs
            Stage stage = new Stage();
            stage.setTitle("Visualizador de Logs");
            stage.setScene(new Scene(root, 900, 700));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (Exception e) {
            AlertUtil.showError("Erro ao abrir logs", e.getMessage());
        }
    }
    
    @FXML
    private void sair() {
        // Encerra o serviço de atualização
        if (refreshService != null) {
            refreshService.shutdown();
        }
        
        // Fecha a aplicação
        Platform.exit();
    }
}
`;

console.log("\nDashboardController.java (Controlador principal):");
console.log(dashboardControllerJava);

// Layout FXML do Dashboard
const dashboardFxml = `
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.chart.PieChart?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.geometry.Insets?>
<?import javafx.scene.image.ImageView?>
<?import javafx.scene.image.Image?>
<?import javafx.scene.text.Font?>

<BorderPane fx:id="mainPane" xmlns="http://javafx.com/javafx/11.0.1" xmlns:fx="http://javafx.com/fxml/1" 
            fx:controller="com.contabilidade.nasajon.gui.controller.DashboardController">
   <top>
      <VBox>
         <MenuBar>
            <Menu text="Arquivo">
               <MenuItem text="Configurações" onAction="#abrirConfiguracoes" />
               <SeparatorMenuItem />
               <MenuItem text="Sair" onAction="#sair" />
            </Menu>
            <Menu text="Lotes">
               <MenuItem text="Gerar Novo Lote" onAction="#gerarNovoLote" />
               <MenuItem text="Processar Lotes Pendentes" onAction="#processarLotesPendentes" />
            </Menu>
            <Menu text="Ferramentas">
               <MenuItem text="Visualizar Logs" onAction="#abrirLogs" />
            </Menu>
            <Menu text="Ajuda">
               <MenuItem text="Sobre" />
            </Menu>
         </MenuBar>
         
         <ToolBar>
            <Button onAction="#gerarNovoLote">
               <graphic>
                  <ImageView>
                     <Image url="@/images/icons/new.png" requestedWidth="16" requestedHeight="16" />
                  </ImageView>
               </graphic>
               <tooltip>
                  <Tooltip text="Gerar Novo Lote" />
               </tooltip>
            </Button>
            <Button onAction="#processarLotesPendentes">
               <graphic>
                  <ImageView>
                     <Image url="@/images/icons/send.png" requestedWidth="16" requestedHeight="16" />
                  </ImageView>
               </graphic>
               <tooltip>
                  <Tooltip text="Processar Lotes Pendentes" />
               </tooltip>
            </Button>
            <Separator orientation="VERTICAL" />
            <Button onAction="#carregarDados">
               <graphic>
                  <ImageView>
                     <Image url="@/images/icons/refresh.png" requestedWidth="16" requestedHeight="16" />
                  </ImageView>
               </graphic>
               <tooltip>
                  <Tooltip text="Atualizar Dados" />
               </tooltip>
            </Button>
            <Separator orientation="VERTICAL" />
            <Button onAction="#abrirConfiguracoes">
               <graphic>
                  <ImageView>
                     <Image url="@/images/icons/settings.png" requestedWidth="16" requestedHeight="16" />
                  </ImageView>
               </graphic>
               <tooltip>
                  <Tooltip text="Configurações" />
               </tooltip>
            </Button>
         </ToolBar>
      </VBox>
   </top>
   
   <center>
      <SplitPane dividerPositions="0.3" orientation="VERTICAL">
         <VBox spacing="10">
            <padding>
               <Insets top="10" right="10" bottom="10" left="10" />
            </padding>
            
            <Label text="Painel de Controle - Envio de Lotes Contábeis para Nasajon" style="-fx-font-weight: bold;">
               <font>
                  <Font size="18.0" />
               </font>
            </Label>
            
            <HBox spacing="20" alignment="CENTER">
               <VBox alignment="CENTER" styleClass="dashboard-card">
                  <Label text="Total de Lotes" styleClass="card-title" />
                  <Label fx:id="totalLotesLabel" text="0" styleClass="card-value" />
               </VBox>
               
               <VBox alignment="CENTER" styleClass="dashboard-card">
                  <Label text="Lotes Pendentes" styleClass="card-title" />
                  <Label fx:id="lotesPendentesLabel" text="0" styleClass="card-value" />
               </VBox>
               
               <VBox alignment="CENTER" styleClass="dashboard-card">
                  <Label text="Lotes Enviados" styleClass="card-title" />
                  <Label fx:id="lotesEnviadosLabel" text="0" styleClass="card-value" />
               </VBox>
               
               <VBox alignment="CENTER" styleClass="dashboard-card">
                  <Label text="Lotes com Erro" styleClass="card-title" />
                  <Label fx:id="lotesErroLabel" text="0" styleClass="card-value" />
               </VBox>
               
               <PieChart fx:id="statusChart" title="Status dos Lotes" legendVisible="true" />
            </HBox>
         </VBox>
         
         <VBox>
            <padding>
               <Insets top="10" right="10" bottom="10" left="10" />
            </padding>
            
            <Label text="Lotes Recentes" style="-fx-font-weight: bold;">
               <font>
                  <Font size="14.0" />
               </font>
            </Label>
            
            <TableView fx:id="lotesTable" VBox.vgrow="ALWAYS">
               <columns>
                  <TableColumn fx:id="codigoLoteColumn" text="Código do Lote" prefWidth="150" />
                  <TableColumn fx:id="statusColumn" text="Status" prefWidth="100" />
                  <TableColumn fx:id="dataGeracaoColumn" text="Data de Geração" prefWidth="150" />
                  <TableColumn fx:id="dataEnvioColumn" text="Data de Envio" prefWidth="150" />
               </columns>
               <placeholder>
                  <Label text="Nenhum lote encontrado" />
               </placeholder>
            </TableView>
         </VBox>
      </SplitPane>
   </center>
   
   <bottom>
      <HBox alignment="CENTER_LEFT" spacing="10" style="-fx-background-color: #f0f0f0; -fx-padding: 5;">
         <Label text="Sistema de Envio de Lotes - Contabilidade para Nasajon" />
         <Pane HBox.hgrow="ALWAYS" />
         <Label text="Última atualização: " />
         <Label fx:id="ultimaAtualizacaoLabel" text="--/--/---- --:--:--" />
      </HBox>
   </bottom>
</BorderPane>
`;

console.log("\nDashboard.fxml (Layout da tela principal):");
console.log(dashboardFxml);

// CSS da aplicação
const applicationCss = `
/* Estilos gerais */
.root {
    -fx-font-family: "Segoe UI", Arial, sans-serif;
    -fx-font-size: 12px;
    -fx-background-color: white;
}

/* Estilos para os cards do dashboard */
.dashboard-card {
    -fx-background-color: white;
    -fx-border-color: #e0e0e0;
    -fx-border-radius: 5px;
    -fx-padding: 15px;
    -fx-min-width: 150px;
    -fx-min-height: 100px;
    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);
}

.card-title {
    -fx-font-size: 14px;
    -fx-text-fill: #555555;
}

.card-value {
    -fx-font-size: 24px;
    -fx-font-weight: bold;
    -fx-text-fill: #2c3e50;
}

/* Estilos para a tabela */
.table-view {
    -fx-background-color: transparent;
}

.table-view .column-header {
    -fx-background-color: #f5f5f5;
    -fx-font-weight: bold;
}

.table-row-cell:selected {
    -fx-background-color: #3498db;
    -fx-text-fill: white;
}

/* Estilos para status */
.status-pendente {
    -fx-text-fill: #f39c12;
}

.status-processando {
    -fx-text-fill: #3498db;
}

.status-enviado {
    -fx-text-fill: #2ecc71;
}

.status-erro {
    -fx-text-fill: #e74c3c;
}

/* Estilos para botões */
.button {
    -fx-background-radius: 3px;
}

.button:hover {
    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 0);
}
`;

console.log("\napplication.css (Estilos da aplicação):");
console.log(applicationCss);

// Tela de configuração
const configuracaoFxml = `
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.geometry.Insets?>

<VBox spacing="10" xmlns="http://javafx.com/javafx/11.0.1" xmlns:fx="http://javafx.com/fxml/1" 
      fx:controller="com.contabilidade.nas  xmlns:fx="http://javafx.com/fxml/1" 
      fx:controller="com.contabilidade.nasajon.gui.controller.ConfiguracaoController">
   <padding>
      <Insets top="20" right="20" bottom="20" left="20" />
   </padding>
   
   <Label text="Configurações do Sistema" style="-fx-font-weight: bold; -fx-font-size: 16px;" />
   
   <Separator />
   
   <TabPane tabClosingPolicy="UNAVAILABLE">
      <Tab text="Conexão com Banco de Dados">
         <GridPane vgap="10" hgap="10">
            <padding>
               <Insets top="10" right="10" bottom="10" left="10" />
            </padding>
            
            <Label text="Servidor:" GridPane.rowIndex="0" GridPane.columnIndex="0" />
            <TextField fx:id="servidorField" GridPane.rowIndex="0" GridPane.columnIndex="1" />
            
            <Label text="Porta:" GridPane.rowIndex="1" GridPane.columnIndex="0" />
            <TextField fx:id="portaField" GridPane.rowIndex="1" GridPane.columnIndex="1" />
            
            <Label text="Nome do Banco:" GridPane.rowIndex="2" GridPane.columnIndex="0" />
            <TextField fx:id="bancoField" GridPane.rowIndex="2" GridPane.columnIndex="1" />
            
            <Label text="Usuário:" GridPane.rowIndex="3" GridPane.columnIndex="0" />
            <TextField fx:id="usuarioField" GridPane.rowIndex="3" GridPane.columnIndex="1" />
            
            <Label text="Senha:" GridPane.rowIndex="4" GridPane.columnIndex="0" />
            <PasswordField fx:id="senhaField" GridPane.rowIndex="4" GridPane.columnIndex="1" />
            
            <Button text="Testar Conexão" onAction="#testarConexao" 
                    GridPane.rowIndex="5" GridPane.columnIndex="1" GridPane.halignment="RIGHT" />
         </GridPane>
      </Tab>
      
      <Tab text="API Nasajon">
         <GridPane vgap="10" hgap="10">
            <padding>
               <Insets top="10" right="10" bottom="10" left="10" />
            </padding>
            
            <Label text="URL Base:" GridPane.rowIndex="0" GridPane.columnIndex="0" />
            <TextField fx:id="urlBaseField" GridPane.rowIndex="0" GridPane.columnIndex="1" />
            
            <Label text="Usuário API:" GridPane.rowIndex="1" GridPane.columnIndex="0" />
            <TextField fx:id="usuarioApiField" GridPane.rowIndex="1" GridPane.columnIndex="1" />
            
            <Label text="Senha API:" GridPane.rowIndex="2" GridPane.columnIndex="0" />
            <PasswordField fx:id="senhaApiField" GridPane.rowIndex="2" GridPane.columnIndex="1" />
            
            <Label text="Timeout (ms):" GridPane.rowIndex="3" GridPane.columnIndex="0" />
            <TextField fx:id="timeoutField" GridPane.rowIndex="3" GridPane.columnIndex="1" />
            
            <Button text="Testar API" onAction="#testarApi" 
                    GridPane.rowIndex="4" GridPane.columnIndex="1" GridPane.halignment="RIGHT" />
         </GridPane>
      </Tab>
      
      <Tab text="Configurações de Lote">
         <GridPane vgap="10" hgap="10">
            <padding>
               <Insets top="10" right="10" bottom="10" left="10" />
            </padding>
            
            <Label text="Intervalo de Processamento (min):" GridPane.rowIndex="0" GridPane.columnIndex="0" />
            <TextField fx:id="intervaloField" GridPane.rowIndex="0" GridPane.columnIndex="1" />
            
            <Label text="Tamanho Máximo do Lote:" GridPane.rowIndex="1" GridPane.columnIndex="0" />
            <TextField fx:id="tamanhoLoteField" GridPane.rowIndex="1" GridPane.columnIndex="1" />
            
            <Label text="Retenção de Logs (dias):" GridPane.rowIndex="2" GridPane.columnIndex="0" />
            <TextField fx:id="retencaoLogsField" GridPane.rowIndex="2" GridPane.columnIndex="1" />
            
            <CheckBox fx:id="processamentoAutomaticoCheck" text="Habilitar processamento automático" 
                      GridPane.rowIndex="3" GridPane.columnIndex="0" GridPane.columnSpan="2" />
                      
            <CheckBox fx:id="notificacaoErrosCheck" text="Enviar notificações de erro por e-mail" 
                      GridPane.rowIndex="4" GridPane.columnIndex="0" GridPane.columnSpan="2" />
         </GridPane>
      </Tab>
   </TabPane>
   
   <HBox spacing="10" alignment="CENTER_RIGHT">
      <Button text="Cancelar" onAction="#cancelar" />
      <Button text="Salvar" onAction="#salvar" defaultButton="true" />
   </HBox>
</VBox>
`;

console.log("\nConfiguracao.fxml (Tela de configurações):");
console.log(configuracaoFxml);

// Controlador de configuração
const configuracaoControllerJava = `
package com.contabilidade.nasajon.gui.controller;

import com.contabilidade.nasajon.config.DatabaseConfig;
import com.contabilidade.nasajon.config.NasajonApiConfig;
import com.contabilidade.nasajon.gui.util.AlertUtil;
import com.contabilidade.nasajon.service.NasajonApiService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;

@Component
public class ConfiguracaoController implements Initializable {

    @Autowired
    private Environment env;
    
    @Autowired
    private NasajonApiService apiService;
    
    @FXML
    private TextField servidorField;
    
    @FXML
    private TextField portaField;
    
    @FXML
    private TextField bancoField;
    
    @FXML
    private TextField usuarioField;
    
    @FXML
    private PasswordField senhaField;
    
    @FXML
    private TextField urlBaseField;
    
    @FXML
    private TextField usuarioApiField;
    
    @FXML
    private PasswordField senhaApiField;
    
    @FXML
    private TextField timeoutField;
    
    @FXML
    private TextField intervaloField;
    
    @FXML
    private TextField tamanhoLoteField;
    
    @FXML
    private TextField retencaoLogsField;
    
    @FXML
    private CheckBox processamentoAutomaticoCheck;
    
    @FXML
    private CheckBox notificacaoErrosCheck;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Carrega as configurações atuais
        carregarConfiguracoes();
    }
    
    private void carregarConfiguracoes() {
        // Carrega configurações do banco de dados
        String url = env.getProperty("spring.datasource.url", "");
        
        // Extrai informações da URL de conexão
        if (url.contains("jdbc:sqlserver://")) {
            String serverInfo = url.replace("jdbc:sqlserver://", "");
            String[] parts = serverInfo.split(";");
            
            if (parts.length > 0 && parts[0].contains(":")) {
                String[] serverParts = parts[0].split(":");
                servidorField.setText(serverParts[0]);
                portaField.setText(serverParts[1]);
            }
            
            for (String part : parts) {
                if (part.startsWith("databaseName=")) {
                    bancoField.setText(part.replace("databaseName=", ""));
                    break;
                }
            }
        }
        
        usuarioField.setText(env.getProperty("spring.datasource.username", ""));
        senhaField.setText(env.getProperty("spring.datasource.password", ""));
        
        // Carrega configurações da API Nasajon
        urlBaseField.setText(env.getProperty("nasajon.api.url-base", ""));
        usuarioApiField.setText(env.getProperty("nasajon.api.usuario", ""));
        senhaApiField.setText(env.getProperty("nasajon.api.senha", ""));
        timeoutField.setText(env.getProperty("nasajon.api.timeout", "30000"));
        
        // Carrega configurações de lote
        intervaloField.setText(env.getProperty("lote.intervalo-processamento", "15"));
        tamanhoLoteField.setText(env.getProperty("lote.tamanho-maximo", "100"));
        retencaoLogsField.setText(env.getProperty("lote.retencao-logs", "30"));
        
        processamentoAutomaticoCheck.setSelected(
            Boolean.parseBoolean(env.getProperty("lote.processamento-automatico", "true")));
            
        notificacaoErrosCheck.setSelected(
            Boolean.parseBoolean(env.getProperty("lote.notificacao-erros", "false")));
    }
    
    @FXML
    private void testarConexao() {
        String servidor = servidorField.getText();
        String porta = portaField.getText();
        String banco = bancoField.getText();
        String usuario = usuarioField.getText();
        String senha = senhaField.getText();
        
        if (servidor.isEmpty() || porta.isEmpty() || banco.isEmpty() || usuario.isEmpty()) {
            AlertUtil.showWarning("Campos Incompletos", "Preencha todos os campos de conexão.");
            return;
        }
        
        try {
            // Constrói a URL de conexão
            String url = String.format(
                "jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true",
                servidor, porta, banco);
                
            // Tenta estabelecer conexão
            Connection conn = DriverManager.getConnection(url, usuario, senha);
            conn.close();
            
            AlertUtil.showInfo("Conexão Bem-Sucedida", "Conexão com o banco de dados estabelecida com sucesso!");
            
        } catch (Exception e) {
            AlertUtil.showError("Erro de Conexão", "Não foi possível conectar ao banco de dados: " + e.getMessage());
        }
    }
    
    @FXML
    private void testarApi() {
        String urlBase = urlBaseField.getText();
        String usuarioApi = usuarioApiField.getText();
        String senhaApi = senhaApiField.getText();
        
        if (urlBase.isEmpty() || usuarioApi.isEmpty() || senhaApi.isEmpty()) {
            AlertUtil.showWarning("Campos Incompletos", "Preencha todos os campos da API.");
            return;
        }
        
        try {
            // Configura temporariamente a API com os novos valores
            NasajonApiConfig tempConfig = new NasajonApiConfig();
            tempConfig.setUrlBase(urlBase);
            tempConfig.setUsuario(usuarioApi);
            tempConfig.setSenha(senhaApi);
            
            // Tenta autenticar
            String token = apiService.testarAutenticacao(tempConfig);
            
            if (token != null && !token.isEmpty()) {
                AlertUtil.showInfo("Autenticação Bem-Sucedida", "Conexão com a API Nasajon estabelecida com sucesso!");
            } else {
                AlertUtil.showWarning("Autenticação Falhou", "Não foi possível autenticar com as credenciais fornecidas.");
            }
            
        } catch (Exception e) {
            AlertUtil.showError("Erro de API", "Não foi possível conectar à API Nasajon: " + e.getMessage());
        }
    }
    
    @FXML
    private void salvar() {
        try {
            // Salva as configurações em um arquivo de propriedades
            PropertiesWriter writer = new PropertiesWriter("application.properties");
            
            // Configurações de banco de dados
            String url = String.format(
                "jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true",
                servidorField.getText(), portaField.getText(), bancoField.getText());
                
            writer.setProperty("spring.datasource.url", url);
            writer.setProperty("spring.datasource.username", usuarioField.getText());
            writer.setProperty("spring.datasource.password", senhaField.getText());
            writer.setProperty("spring.datasource.driver-class-name", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            // Configurações da API Nasajon
            writer.setProperty("nasajon.api.url-base", urlBaseField.getText());
            writer.setProperty("nasajon.api.usuario", usuarioApiField.getText());
            writer.setProperty("nasajon.api.senha", senhaApiField.getText());
            writer.setProperty("nasajon.api.timeout", timeoutField.getText());
            
            // Configurações de lote
            writer.setProperty("lote.intervalo-processamento", intervaloField.getText());
            writer.setProperty("lote.tamanho-maximo", tamanhoLoteField.getText());
            writer.setProperty("lote.retencao-logs", retencaoLogsField.getText());
            writer.setProperty("lote.processamento-automatico", String.valueOf(processamentoAutomaticoCheck.isSelected()));
            writer.setProperty("lote.notificacao-erros", String.valueOf(notificacaoErrosCheck.isSelected()));
            
            writer.save();
            
            AlertUtil.showInfo("Configurações Salvas", "As configurações foram salvas com sucesso. Reinicie a aplicação para aplicar as alterações.");
            
            // Fecha a janela
            ((Stage) servidorField.getScene().getWindow()).close();
            
        } catch (Exception e) {
            AlertUtil.showError("Erro ao Salvar", "Não foi possível salvar as configurações: " + e.getMessage());
        }
    }
    
    @FXML
    private void cancelar() {
        // Fecha a janela sem salvar
        ((Stage) servidorField.getScene().getWindow()).close();
    }
}
`;

console.log("\nConfiguracaoController.java (Controlador de configurações):");
console.log(configuracaoControllerJava);

// Visualizador de logs
const logViewFxml = `
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.geometry.Insets?>

<VBox spacing="10" xmlns="http://javafx.com/javafx/11.0.1" xmlns:fx="http://javafx.com/fxml/1" 
      fx:controller="com.contabilidade.nasajon.gui.controller.LogViewController">
   <padding>
      <Insets top="20" right="20" bottom="20" left="20" />
   </padding>
   
   <HBox spacing="10" alignment="CENTER_LEFT">
      <Label text="Visualizador de Logs" style="-fx-font-weight: bold; -fx-font-size: 16px;" />
      <Pane HBox.hgrow="ALWAYS" />
      <Label text="Filtrar por:" />
      <ComboBox fx:id="nivelLogCombo" promptText="Nível" />
      <DatePicker fx:id="dataLogPicker" promptText="Data" />
      <TextField fx:id="filtroTextoField" promptText="Texto" prefWidth="200" />
      <Button text="Filtrar" onAction="#aplicarFiltros" />
      <Button text="Limpar Filtros" onAction="#limparFiltros" />
   </HBox>
   
   <Separator />
   
   <TextArea fx:id="logTextArea" VBox.vgrow="ALWAYS" editable="false" wrapText="true" 
             style="-fx-font-family: 'Courier New'; -fx-font-size: 12px;" />
   
   <HBox spacing="10" alignment="CENTER_RIGHT">
      <Button text="Atualizar" onAction="#carregarLogs" />
      <Button text="Exportar Logs" onAction="#exportarLogs" />
      <Button text="Fechar" onAction="#fechar" />
   </HBox>
</VBox>
`;

console.log("\nLogView.fxml (Visualizador de logs):");
console.log(logViewFxml);

// Tela de detalhes do lote
const loteDetailsFxml = `
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.geometry.Insets?>

<VBox spacing="10" xmlns="http://javafx.com/javafx/11.0.1" xmlns:fx="http://javafx.com/fxml/1" 
      fx:controller="com.contabilidade.nasajon.gui.controller.LoteDetailsController">
   <padding>
      <Insets top="20" right="20" bottom="20" left="20" />
   </padding>
   
   <Label fx:id="tituloLabel" text="Detalhes do Lote: [Código]" style="-fx-font-weight: bold; -fx-font-size: 16px;" />
   
   <Separator />
   
   <GridPane hgap="10" vgap="10">
      <columnConstraints>
         <ColumnConstraints minWidth="150" />
         <ColumnConstraints hgrow="ALWAYS" />
      </columnConstraints>
      
      <Label text="Código do Lote:" GridPane.rowIndex="0" GridPane.columnIndex="0" style="-fx-font-weight: bold;" />
      <Label fx:id="codigoLoteLabel" text="-" GridPane.rowIndex="0" GridPane.columnIndex="1" />
      
      <Label text="Status:" GridPane.rowIndex="1" GridPane.columnIndex="0" style="-fx-font-weight: bold;" />
      <Label fx:id="statusLabel" text="-" GridPane.rowIndex="1" GridPane.columnIndex="1" />
      
      <Label text="Data de Geração:" GridPane.rowIndex="2" GridPane.columnIndex="0" style="-fx-font-weight: bold;" />
      <Label fx:id="dataGeracaoLabel" text="-" GridPane.rowIndex="2" GridPane.columnIndex="1" />
      
      <Label text="Data de Envio:" GridPane.rowIndex="3" GridPane.columnIndex="0" style="-fx-font-weight: bold;" />
      <Label fx:id="dataEnvioLabel" text="-" GridPane.rowIndex="3" GridPane.columnIndex="1" />
      
      <Label text="Código de Retorno:" GridPane.rowIndex="4" GridPane.columnIndex="0" style="-fx-font-weight: bold;" />
      <Label fx:id="codigoRetornoLabel" text="-" GridPane.rowIndex="4" GridPane.columnIndex="1" />
   </GridPane>
   
   <Label text="Mensagem de Erro:" style="-fx-font-weight: bold;" fx:id="erroTituloLabel" visible="false" />
   <TextArea fx:id="mensagemErroArea" editable="false" wrapText="true" prefHeight="100" visible="false" />
   
   <Label text="Lançamentos Contábeis:" style="-fx-font-weight: bold;" />
   <TableView fx:id="lancamentosTable" VBox.vgrow="ALWAYS">
      <columns>
         <TableColumn fx:id="idLancamentoColumn" text="ID" prefWidth="80" />
         <TableColumn fx:id="contaDebitoColumn" text="Conta Débito" prefWidth="150" />
         <TableColumn fx:id="contaCreditoColumn" text="Conta Crédito" prefWidth="150" />
         <TableColumn fx:id="valorColumn" text="Valor" prefWidth="100" />
         <TableColumn fx:id="historicColumn" text="Histórico" prefWidth="300" />
         <TableColumn fx:id="dataLancamentoColumn" text="Data" prefWidth="120" />
      </columns>
      <placeholder>
         <Label text="Nenhum lançamento encontrado" />
      </placeholder>
   </TableView>
   
   <HBox spacing="10" alignment="CENTER_RIGHT">
      <Button fx:id="reprocessarButton" text="Reprocessar Lote" onAction="#reprocessarLote" visible="false" />
      <Button fx:id="consultarStatusButton" text="Consultar Status no Nasajon" onAction="#consultarStatus" />
      <Button text="Exportar Dados" onAction="#exportarDados" />
      <Button text="Fechar" onAction="#fechar" />
   </HBox>
</VBox>
`;

console.log("\nLoteDetails.fxml (Detalhes do lote):");
console.log(loteDetailsFxml);

// Classe utilitária para alertas
const alertUtilJava = `
package com.contabilidade.nasajon.gui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AlertUtil {

    /**
     * Exibe um alerta informativo
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Exibe um alerta de aviso
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Exibe um alerta de erro
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Exibe um alerta de erro com detalhes da exceção
     */
    public static void showError(String title, String message, Exception ex) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Cria área de texto para o stack trace
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();
        
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);
        
        alert.getDialogPane().setExpandableContent(expContent);
        
        alert.showAndWait();
    }
    
    /**
     * Exibe um diálogo de confirmação
     * @return true se o usuário confirmar, false caso contrário
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        return alert.showAndWait().get() == ButtonType.OK;
    }
}
`;

console.log("\nAlertUtil.java (Utilitário para alertas):");
console.log(alertUtilJava);

console.log("\nDescrição da Interface Gráfica:");
console.log("1. Dashboard principal com estatísticas e visualização de lotes");
console.log("2. Tela de configuração para banco de dados SQL Server e API Nasajon");
console.log("3. Visualizador de logs para monitoramento e diagnóstico");
console.log("4. Tela de detalhes de lote para visualizar e gerenciar lotes específicos");
console.log("5. Funcionalidades de geração e processamento de lotes via interface gráfica");
console.log("6. Suporte a operações em segundo plano para não bloquear a interface");
console.log("7. Notificações visuais para feedback ao usuário");
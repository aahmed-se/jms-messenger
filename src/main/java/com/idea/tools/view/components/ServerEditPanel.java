package com.idea.tools.view.components;

import com.idea.tools.dto.ConnectionType;
import com.idea.tools.dto.ServerDto;
import com.idea.tools.dto.ServerType;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.JBColor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.util.Objects;

import static com.idea.tools.App.jmsService;
import static com.idea.tools.App.serverService;
import static com.idea.tools.utils.GuiUtils.createNumberInputField;
import static com.idea.tools.utils.GuiUtils.simpleListener;
import static com.idea.tools.utils.Utils.getOrDefault;
import static com.idea.tools.utils.Utils.toInteger;
import static com.intellij.ui.JBColor.GREEN;
import static com.intellij.ui.JBColor.RED;
import static com.intellij.ui.ScrollPaneFactory.createScrollPane;
import static java.awt.BorderLayout.CENTER;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ServerEditPanel extends JPanel {

    private static final String CONNECTION_SUCCESS_TEXT = "Success";
    private static final String CONNECTION_FAIL_TEXT = "Fail";
    private static final JBColor CONNECTION_SUCCESS_COLOR = GREEN;
    private static final JBColor CONNECTION_FAIL_COLOR = RED;

    private ServerDto server;

    private JPanel rootPanel;
    private JComboBox<ServerType> typeComboBox;
    private JComboBox<ConnectionType> connectionType;
    private JTextField hostField;
    private JFormattedTextField portField;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton testConnectionButton;
    @Getter
    private JButton cancelButton;
    @Getter
    private JButton saveButton;
    private JTextField idField;
    private JTextField nameField;
    private JLabel connectionStatus;
    private JTextArea connectionDetails;

    public ServerEditPanel() {
        this(new ServerDto());
    }

    public ServerEditPanel(ServerDto server) {
        this.server = server;
        render();
    }

    public void setNewValue(ServerDto server) {
        this.server = server;
        setValues();
        enableButtons();
    }

    private void render() {
        add(createScrollPane(rootPanel), CENTER);

        connectionDetails.setLineWrap(true);
        connectionDetails.setColumns(2);

        setValues();
        updateNameFieldValue();
        enableButtons();

        DocumentListener validator = simpleListener(event -> enableButtons());

        typeComboBox.addActionListener(event -> updateConnectionTypeModel());

        hostField.addActionListener(event -> updateNameFieldValue());
        hostField.getDocument().addDocumentListener(simpleListener(event -> updateNameFieldValue()));
        hostField.getDocument().addDocumentListener(validator);

        portField.addActionListener(event -> updateNameFieldValue());
        portField.getDocument().addDocumentListener(simpleListener(event -> updateNameFieldValue()));
        portField.getDocument().addDocumentListener(validator);

        nameField.addActionListener(event -> server.setNameIsAutogenerated(false));
        nameField.getDocument().addDocumentListener(simpleListener(event -> server.setNameIsAutogenerated(false)));

        testConnectionButton.addActionListener(event -> {
            ServerDto server = new ServerDto();
            fillServer(server);
            emptyStatus();
            try {
                jmsService().testConnection(server);
                successStatus();
            } catch (Exception ex) {
                ex.printStackTrace();
                failStatus(ex);
            }
        });

        saveButton.addActionListener(event -> {
            fillServer(server);
            serverService().saveOrUpdate(server);
        });

        cancelButton.addActionListener(event -> {
            setValues();
            enableButtons();
        });
    }

    private void emptyStatus() {
        connectionStatus.setText("");
        connectionStatus.setVisible(false);

        connectionDetails.setText("");
        connectionDetails.setVisible(false);
    }

    private void successStatus() {
        connectionStatus.setText(CONNECTION_SUCCESS_TEXT);
        connectionStatus.setForeground(CONNECTION_SUCCESS_COLOR);
        connectionStatus.setVisible(true);
    }

    private void failStatus(@NotNull Exception ex) {
        connectionStatus.setText(CONNECTION_FAIL_TEXT);
        connectionStatus.setForeground(CONNECTION_FAIL_COLOR);
        connectionStatus.setVisible(true);

        connectionDetails.setText(ex.getMessage());
        connectionDetails.setVisible(true);
    }

    private void enableButtons() {
        boolean requiredFieldsAreFilled = requiredFieldsAreFilled();
        saveButton.setEnabled(requiredFieldsAreFilled);
        testConnectionButton.setEnabled(requiredFieldsAreFilled);
    }

    private boolean requiredFieldsAreFilled() {
        return isNotEmpty(hostField.getText()) && isNotEmpty(portField.getText());
    }

    private void fillServer(ServerDto server) {
        server.setType(typeComboBox.getItemAt(typeComboBox.getSelectedIndex()));
        server.setConnectionType(connectionType.getItemAt(connectionType.getSelectedIndex()));
        server.setHost(hostField.getText());
        server.setPort(toInteger(portField.getText()));
        server.setLogin(loginField.getText());
        server.setPassword(String.valueOf(passwordField.getPassword()));
        server.setName(nameField.getText());
    }

    private void setValues() {
        idField.setText(server.getId());
        typeComboBox.setSelectedItem(getOrDefault(server.getType(), ServerType.ARTEMIS));
        updateConnectionTypeModel();
        if (server.getConnectionType() != null) {
            connectionType.setSelectedItem(server.getConnectionType());
        }
        hostField.setText(getOrDefault(server.getHost(), "localhost"));
//        portField.setValue(getOrDefault(server.getPort(), 61616));
        portField.setValue(getOrDefault(server.getPort(), 8080));
        nameField.setText(server.getName());
        loginField.setText(server.getLogin());
        passwordField.setText(server.getPassword());
    }

    private void updateNameFieldValue() {
        if (server.isNameIsAutogenerated()) {
            String host = hostField.getText();
            String port = Objects.toString(portField.getValue());
            if (isNotEmpty(host)) {
                String value = host;
                if (isNotEmpty(port)) {
                    value += ":" + port;
                }
                nameField.setText(value);
                server.setNameIsAutogenerated(true);
            }
        }
    }

    private void updateConnectionTypeModel() {
        ConnectionType[] types = typeComboBox.getItemAt(typeComboBox.getSelectedIndex()).getConnectionTypes();
        connectionType.setModel(new DefaultComboBoxModel<>(types));
        if (types.length != 0) {
            connectionType.setSelectedItem(types[0]);
        }
        connectionType.repaint();
    }


    private void createUIComponents() {
        typeComboBox = new ComboBox<>(new EnumComboBoxModel<>(ServerType.class));
        connectionType = new ComboBox<>();
        portField = createNumberInputField();
    }
}

package it.freedomotic.jfrontend.automationeditor;

import it.freedomotic.app.Freedomotic;

import it.freedomotic.core.NaturalLanguageProcessor;

import it.freedomotic.jfrontend.automationeditor.ReactionEditor;

import it.freedomotic.reactions.Command;
import it.freedomotic.reactions.CommandPersistence;

import it.freedomotic.util.I18n.I18n;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author enrico
 */
public class GuessCommandBox
        extends JTextField {

    private Command command = null;
    private ReactionEditor editor;
    private static NaturalLanguageProcessor nlp = new NaturalLanguageProcessor();
    private final JButton btnAdd = new JButton();
    private final JButton btnCustomize;
    private final GuessCommandBox me = this;
    private final String ERROR_MESSAGE;
    private final String INFO_MESSAGE;
    private final I18n I18n;
    
    public GuessCommandBox(I18n i18n, ReactionEditor editor) {        
        super();
        this.I18n = i18n;
        btnCustomize = new JButton(I18n.msg( "edit"));
        ERROR_MESSAGE = I18n.msg("this_command_not_exists");
        INFO_MESSAGE = I18n.msg("write_here_command");
        
        this.editor = editor;
        init();
    }

    public GuessCommandBox(I18n i18n, ReactionEditor editor, Command command) {
        super();
        this.I18n = i18n;
        btnCustomize = new JButton(I18n.msg( "edit"));
        ERROR_MESSAGE = I18n.msg("this_command_not_exists");
        INFO_MESSAGE = I18n.msg("write_here_command");
        
        this.command = command;
        this.editor = editor;
        setEnabled(false);
        init();
    }

    private void init() {
        listen();
    }

    public Command getCommand() {
        return command;
    }

    private void listen() {
        if (command != null) {
            setText(command.getName());
        } else {
            setText(INFO_MESSAGE);
        }

        this.setPreferredSize(new Dimension(300, 30));

        KeyListener keyListener =
                new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent evt) {
                        command = null;
                        setForeground(Color.black);

                        //for (String line : lines) {
                        List<NaturalLanguageProcessor.Rank> mostSimilar = nlp.getMostSimilarCommand(getText(),
                                10);

                        //Command command = mostSimilar.get(0).getCommand();
                        //txtInput.setText(txtInput.getText().replace(line, command.getName()));
                        JPopupMenu menu = new JPopupMenu();

                        //command = mostSimilar.get(0).getCommand();
                        for (final NaturalLanguageProcessor.Rank rank : mostSimilar) {
                            JMenuItem menuItem = new JMenuItem(rank.getCommand().toString());
                            menuItem.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    command = rank.getCommand();
                                    setText(command.getName());
                                }
                            });
                            menu.add(menuItem);
                        }

                        menu.show(evt.getComponent(),
                                evt.getComponent().getX() + (getText().length() * 10),
                                evt.getComponent().getY());
                        requestFocus();

                        new JComboBox();
                    }
                };

        this.addKeyListener(keyListener);

        if (command == null) {
            btnAdd.setText(I18n.msg( "confirm"));
            setToolTipText(I18n.msg( "cmd_box_msg"));
        } else {
            btnAdd.setText(I18n.msg("remove"));
            setToolTipText(command.getDescription());
        }

        this.add(btnAdd);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (isEnabled()) {
                    command = CommandPersistence.getCommand(getText());

                    if (command != null) {
                        setEnabled(false);
                        btnAdd.setText(I18n.msg("remove"));
                        editor.onCommandConfirmed(me);
                    } else {
                        setForeground(Color.red);
                        setText(ERROR_MESSAGE);
                    }
                } else {
                    setEnabled(true);
                    btnAdd.setText(I18n.msg( "confirm"));
                    editor.onCommandCleared(me);
                    command = null;
                    setText(INFO_MESSAGE);
                }
            }
        });

        this.add(btnCustomize);
        btnCustomize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (command != null) {
                    Command c = new Command();
                    c.setName("Edit a command");
                    c.setReceiver("app.actuators.nlautomationseditor.nlautomationseditor.in");
                    c.setProperty("editor", "command");
                    c.setProperty("editable",
                            command.getName()); //the default choice
                    Freedomotic.sendCommand(c);
                    command = null;
                    setText(INFO_MESSAGE);
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        btnAdd.setBounds(getWidth() - 80, (getHeight() / 2) - 10, 70, 20);

        if ((command != null) && isEnabled()) {
            btnCustomize.setVisible(true);
            btnCustomize.setEnabled(true);
            btnCustomize.setBounds(getWidth() - 160, (getHeight() / 2) - 10, 70, 20);
        } else {
            btnCustomize.setVisible(false);
            btnCustomize.setEnabled(false);
        }
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}

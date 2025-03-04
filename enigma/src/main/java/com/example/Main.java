package com.example;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main{
    public static void main(String[] args){  
        int numRotors = 6; // the last rotor is actually the reflector
        // generate the number of indicated rotors, with the given enciphering strings and notches

        
        Rotor[] rotors = new Rotor[numRotors]; 
        ArrayList<String> rotoralphabets = new ArrayList<String>(Arrays.asList("VZBRGITYUPSDNHLXAWMJQOFECK","ESOVPZJAYQUIRHXLNFTGKDCMWB","BDFHJLCPRTXVZNYEIWGAKMUSQO","AJDKSIRUXBLHWTMCQGZNPYFVOE","EKMFLGDQVZNTOWYHXUSPAIBRCJ","EJMZALYXVBWFCRQUONTSPIKHGD"));
        ArrayList<Character> notches = new ArrayList<Character>(Arrays.asList('Q','E','V','J','Z',null));
        for (int i = 0; i < numRotors; i++){
            rotors[i] = new Rotor(rotoralphabets.get(i),notches.get(i));
        }

        //create the frame and set up its size and leyout mechanism
        JFrame frame = new JFrame("Enigma Machine");
        frame.setSize(800,500);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        // a dummy container to hold properties about the wires for other elements to reference when they need to adjust said properties
        JComponent wireContainer = new JComponent() {};
        wireContainer.putClientProperty("CurrentWire","");
        wireContainer.putClientProperty("StoredLetter","");
        wireContainer.putClientProperty("Alphabet", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        wireContainer.putClientProperty("OtherSide", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        // label for the text entry box
        JLabel label = new JLabel("Enter the text you'd like to encipher:");
        // positioning and constraints setup, so that the elements extend to fill all of the grid cells they occupy
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 5;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = 1;
        frame.add(label,constraints);


        constraints.gridx = 2;
        JLabel cipherLabel = new JLabel("Enter a Cipher:");
        frame.add(cipherLabel,constraints);

        constraints.gridx = 3;
        JTextField cipher = new JTextField("00000000");
        cipher.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String[] ab = {"VZBRGITYUPSDNHLXAWMJQOFECK","ESOVPZJAYQUIRHXLNFTGKDCMWB","BDFHJLCPRTXVZNYEIWGAKMUSQO","AJDKSIRUXBLHWTMCQGZNPYFVOE","EKMFLGDQVZNTOWYHXUSPAIBRCJ","EJMZALYXVBWFCRQUONTSPIKHGD"};
                int sum = 0;
                for (Character c : cipher.getText().toCharArray()){
                    sum += Character.getNumericValue(c);
                }
                sum = sum % 26;

                for (int i = 0; i < rotors.length; i++){
                    rotors[i].setAlphabet(ab[i].substring(sum) + ab[i].substring(0,sum));
                }
            }
            
        });
        frame.add(cipher,constraints);

        // text field for test to be enciphered to be entered into
        JTextField textField = new JTextField("Enter the words you'd like to encipher");
        textField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e){
                if (textField.getText().equals("Enter the words you'd like to encipher")){
                    textField.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e){
                if (textField.getText().equals("")) {
                    textField.setText("Enter the words you'd like to encipher");
                }
            }

        });
        textField.putClientProperty("Rotors",rotors);
        textField.putClientProperty("wireContainer",wireContainer);
        textField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String endVal = "";
                for (Character c : textField.getText().toCharArray()){
                    if (c == ' '){
                        endVal += " ";
                    } else {
                        int signal = ((String)((JComponent)((JTextField)e.getSource()).getClientProperty("wireContainer")).getClientProperty("OtherSide")).indexOf(Character.toUpperCase(c));
                        for (Rotor rot : rotors){
                                signal = rot.forward(signal);
                            }
                            for (int i = numRotors-2; i >= 0; i--){
                                signal = rotors[i].backward(signal);
                            }
                        endVal += ((String)((JComponent)((JTextField)e.getSource()).getClientProperty("wireContainer")).getClientProperty("OtherSide")).charAt(signal);
                        // this handles rotating the rotors, it doesn't have the double stepping the original enigma had but that was a design choice on my part
                        for (int i = 1; i < rotors.length-2; i++){
                            if (rotors[i-1].notch == rotors[i-1].alphabet.toCharArray()[0]){
                                rotors[i].rotate(1);
                            }
                        }
                        rotors[0].rotate(1);
                    }
                }

                JTextArea text = new JTextArea("Enciphered Message:\n" + endVal,10,10);
                text.setEditable(false);
                text.setLineWrap(true);
                JOptionPane.showMessageDialog(null,new JScrollPane(text));
                for (Rotor r : rotors){
                    r.rotateTo('A');
                }
                
            }
            
        });

        //code to add the input text box to the frame, along with the grid positioning
        constraints.gridx = 0;
        constraints.gridy = 1;
        frame.add(textField, constraints);
        //resets the width of the following elements to be 1 grid cell instead of 3
        constraints.gridwidth = 1;

        //generates a 
        for (int i = 0; i < numRotors-1; i++){
            JSpinner spinner = new JSpinner(new SpinnerListModel(Arrays.asList('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z')));
            spinner.putClientProperty("Rotor",rotors[i]);
            spinner.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    ((Rotor) spinner.getClientProperty("Rotor")).rotateTo((Character) spinner.getValue());
                }
            });
            constraints.gridx = i;
            constraints.gridy = 3;
            frame.add(spinner, constraints);
            JLabel rotorLabel = new JLabel("Rotor " + (i+1));
            constraints.gridy = 2;
            frame.add(rotorLabel, constraints);
        }

        int counter = 0;
        HashMap buttonArray = new HashMap<Character,JButton>();
        for (Character character : new ArrayList<Character>(Arrays.asList('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'))) {
            constraints.gridx = counter % 2;
            constraints.gridy = (counter / 2) + 4;
            JButton letterButton = new JButton(Character.toString(character));
            letterButton.putClientProperty("wireContainer",wireContainer);
            letterButton.putClientProperty("buttons", buttonArray);
            letterButton.setBackground(new Color(150,170,180));
            letterButton.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Color wireColor;
                    try{
                        java.lang.reflect.Field field = Color.class.getField(((String)((JButton)((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("CurrentWire")).getText()).replace(" wire","").replace(" ",""));
                        wireColor = (Color) field.get(null);
                    } catch (Exception ex) {
                        wireColor = new Color(150,170,180);
                    }
                    if (((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("StoredLetter").equals("")){
                        for (Object button : ((HashMap)((JButton)e.getSource()).getClientProperty("buttons")).values()){
                            if (((JButton)button).getBackground().equals(wireColor)){
                                ((JButton)button).setBackground(new Color(170,170,170));
                            }
                        }
                        ((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).putClientProperty("StoredLetter", letterButton.getText());
                    } else {
                        String temp = ((String)((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("OtherSide"));
                        temp = temp.substring(0,((String)((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("Alphabet")).indexOf((String)letterButton.getText())) + ((String)((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("StoredLetter")) + temp.substring(((String)((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("Alphabet")).indexOf((String)letterButton.getText()) + 1);
                        temp = temp.substring(0,((String)((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("Alphabet")).indexOf(((String)((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("StoredLetter")))) + (String)letterButton.getText() + temp.substring(((String)((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("Alphabet")).indexOf(((String)((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("StoredLetter"))) + 1);
                        ((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).putClientProperty("OtherSide",temp);
                        if (!(((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("CurrentWire").equals(""))) {((JButton)((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("CurrentWire")).setBackground(new Color(150,170,180));}
                        ((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).putClientProperty("CurrentWire","");
                        ((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).putClientProperty("StoredLetter", "");
                    }
                    letterButton.setBackground(wireColor);
                    letterButton.setForeground((calcLum(wireColor.getRed(),wireColor.getGreen(),wireColor.getBlue()) > 1.0) ? new Color(0,0,0) : new Color(255,255,255));
                    //if firstLetter is empty, set it, if not, set the associations and clear it.
                }
                @Override
                public void mousePressed(MouseEvent e) {}
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {}
            });
            frame.add(letterButton, constraints);
            buttonArray.put(character,letterButton);
            counter ++;
        }

        counter = 4;
        for (String colorEach : new ArrayList<String>(Arrays.asList("red","orange","yellow","green","light Gray","blue","black","cyan","gray","magenta","pink","white","dark Gray"))){
            constraints.gridx = 2;
            constraints.gridy = counter;
            constraints.gridwidth = 3;
            JButton wireButton = new JButton(colorEach + " wire");
            wireButton.setBackground(new Color(150,170,180));
            wireButton.putClientProperty("wireContainer",wireContainer);
            wireButton.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    //reset what wire is selected first
                    if (!((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("CurrentWire").equals("")){
                        ((JButton)((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).getClientProperty("CurrentWire")).setBackground(new Color(150,170,180));
                    }
                    ((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).putClientProperty("CurrentWire",wireButton);
                    ((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).putClientProperty("StoredLetter","");
                    wireButton.setBackground(new Color(200,220,230));
                }
                @Override
                public void mousePressed(MouseEvent e) {}
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {}
            });
            frame.add(wireButton, constraints);
            counter++;
        }

        constraints.gridx = 0;
        constraints.gridy = 17;
        constraints.gridwidth = 5;
        JButton wireClear = new JButton("Clear Wire Connections");
        wireClear.setBackground(new Color(150,170,180));
        wireClear.putClientProperty("wireContainer",wireContainer);
        wireClear.putClientProperty("buttons", buttonArray);
        wireClear.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ((JComponent)((JButton)e.getSource()).getClientProperty("wireContainer")).putClientProperty("OtherSide","ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                HashMap<Character,JButton> buttons = (HashMap<Character,JButton>)((JButton)e.getSource()).getClientProperty("buttons");
                for (JButton button : buttons.values()){
                    
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });


        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    System.exit(0);
            }
        });
        frame.setVisible(true);
    }
    private static double calcLum(int r, int g, int b) {
        return ((Double.valueOf(r)/255.0 <= 0.03928) ? (0.2126 * (Double.valueOf(r)/3294.6)) : Math.pow(((Double.valueOf(r)/255.0+0.055)/1.055),2.4)) +
            ((Double.valueOf(g)/255 <= 0.03928) ? (0.7152 * (Double.valueOf(g)/3294.6)) : Math.pow(((Double.valueOf(g)/255+0.055)/1.055),2.4)) + 
            ((Double.valueOf(b)/255 <= 0.03928) ? (0.0722 * ((Double.valueOf(b)/3294.6))) : Math.pow((((Double.valueOf(b)/255+0.055)/1.055)),2.4));
    }

}

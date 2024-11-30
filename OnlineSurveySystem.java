import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyApplication extends Frame implements ActionListener {
    private TextField usernameField, passwordField;
    private Button loginButton, signUpButton, backButton;
    private Label messageLabel, logoLabel; // Added logo label
    private Choice roleChoice; // Dropdown for role selection

    // In-memory user storage
    private List<User> users = new ArrayList<>();
    private List<Survey> surveys = new ArrayList<>();

    public SurveyApplication() {
        // Set up the frame
        setTitle("User Authentication System");
        setSize(400, 500); // Adjusted size for portrait layout
        setLayout(new GridLayout(8, 1)); // Use GridLayout for better organization

        // Create UI components
        usernameField = new TextField(20);
        passwordField = new TextField(20);
        passwordField.setEchoChar('*');
        loginButton = new Button("Login");
        signUpButton = new Button("Sign Up");
        backButton = new Button("Back");
        messageLabel = new Label("");

        // Logo label
        logoLabel = new Label("Survey", Label.CENTER);
        logoLabel.setFont(new Font("Times New Roman", Font.BOLD, 36));
        logoLabel.setForeground(new Color(0, 102, 204)); // Blue color for the logo

        // Set background color to light blue
        setBackground(new Color(0, 255, 051)); // Light blue background

        // Add components to the frame
        add(logoLabel); // Add logo at the top
        add(new Label("Username:"));
        add(usernameField);
        add(new Label("Password:"));
        add(passwordField);
        
        // Add buttons in a panel for better alignment
        Panel buttonPanel = new Panel();
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);
        
        add(buttonPanel);
        
        // Add sign-up prompt
        messageLabel.setAlignment(Label.CENTER); // Center align message label
        messageLabel.setForeground(new Color(0, 102, 204)); // Blue text
        messageLabel.setText("Are you new here? Click 'Sign Up' to create an account.");
        add(messageLabel);

        // Add action listeners
        loginButton.addActionListener(this);
        signUpButton.addActionListener(this);
        
        // Set frame visibility and style
        setVisible(true);

        // Close operation
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    @Override
    public void paint(Graphics g) {
      g.setColor(new Color(0, 255, 051));  // Light blue background color
      g.fillRect(0, 0, getWidth(), getHeight()); // Fill the entire frame with light blue color
    }

    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (e.getSource() == loginButton) {
            User user = authenticate(username, password);
            if (user != null) {
                messageLabel.setText("Login successful! Role: " + user.getRole());
                showRoleBasedMenu(user);
            } else {
                messageLabel.setText("Invalid credentials. Please try again.");
            }
        } else if (e.getSource() == signUpButton) {
            showSignUpForm();
        } else if (e.getSource() == backButton) {
            showLoginForm();
        }
    }

    private void showSignUpForm() {
        // Clear current components
        removeAll();
        
        // Create a new layout for sign-up form
        setLayout(new GridLayout(5, 1));
        
        add(new Label("Choose your role:"));
        
        roleChoice = new Choice();
        roleChoice.add("USER");
        roleChoice.add("ADMIN");
        
        add(roleChoice);
        
        add(new Label("Username:"));
        add(usernameField);
        
        add(new Label("Password:"));
        add(passwordField);

        Button confirmSignUpButton = new Button("Create Account");
        
        confirmSignUpButton.addActionListener(e -> {
            String selectedRole = roleChoice.getSelectedItem();
            registerUser(usernameField.getText(), passwordField.getText(), selectedRole);
            messageLabel.setText("User registered successfully! You can now log in.");
            resetLoginForm();
            showLoginForm();
         });
         
         add(confirmSignUpButton);
         add(messageLabel);

         validate();
         repaint();
     }

    private void showLoginForm() {
       removeAll();  // Clear current components
       setLayout(new GridLayout(6, 1));  // Reset layout

       // Re-add login components
       add(new Label("Username:"));
       add(usernameField);
       add(new Label("Password:"));
       add(passwordField);

       Panel buttonPanel = new Panel();
       buttonPanel.add(loginButton);
       buttonPanel.add(signUpButton);
       
       add(buttonPanel);

       messageLabel.setText("Are you new here? Click 'Sign Up' to create an account.");
       messageLabel.setAlignment(Label.CENTER); // Center align message label
       add(messageLabel); 

       validate();  // Refresh UI
       repaint();   // Update UI display
   }

    private void resetLoginForm() {
       usernameField.setText("");  // Clear username field
       passwordField.setText("");  // Clear password field
   }

    private void registerUser(String username, String password, String selectedRole) {
       Role role = Role.valueOf(selectedRole);  // Convert string to Role enum value
       users.add(new User(username, password, role));
    }

    private User authenticate(String username, String password) {
       for (User user : users) {
           if (user.getUsername().equals(username) && user.getPassword().equals(password)) {  // Simple plain text check
               return user;
           }
       }
       return null; // Authentication failed
   }

    private void showRoleBasedMenu(User user) {
       removeAll();  // Clear current components

       setLayout(new GridLayout(5, 1));  // Reset layout

       if (user.getRole() == Role.ADMIN) {
           Button createSurveyButton = new Button("Create Survey");
           Button editSurveyOptionsButton = new Button("Edit Survey Options"); 
           Button viewResponsesButton = new Button("View Survey Responses"); 
           
           createSurveyButton.addActionListener(e -> createSurvey());
           editSurveyOptionsButton.addActionListener(e -> editSurveyOptions()); 
           viewResponsesButton.addActionListener(e -> viewResponses()); 
           
           add(createSurveyButton);
           add(editSurveyOptionsButton); 
           add(viewResponsesButton); 
           
           messageLabel.setText("Admin Menu - Create Surveys and View Responses");
           
       } else if (user.getRole() == Role.USER) {
           Button respondToSurveyButton = new Button("Respond to Survey");
           respondToSurveyButton.addActionListener(e -> respondToSurvey());
           
           add(respondToSurveyButton);
           
           messageLabel.setText("User Menu - View Surveys");
           viewSurveys();  // Automatically show surveys for users.
       }

       backButton.addActionListener(e -> showLoginForm()); 
       
       Panel backPanel = new Panel();
       backPanel.add(backButton); 
       
       add(backPanel); 
       
       messageLabel.setAlignment(Label.CENTER); 
       add(messageLabel);  // Add message label back

       validate();  // Refresh UI
    }

    private void createSurvey() {
      String surveyTitle = DialogUtils.showInputDialog(this, "Enter Survey Title:", "Create Survey");

      if (surveyTitle != null && !surveyTitle.trim().isEmpty()) {
          String optionsInput = DialogUtils.showInputDialog(this,
              "Enter options separated by commas:", "Create Survey Options");
          List<String> options = List.of(optionsInput.split(",\\s*")); // Split input into options

          surveys.add(new Survey(surveyTitle, options));
          messageLabel.setText("Survey created: " + surveyTitle + " with options: " + optionsInput);
      } else {
          messageLabel.setText("Survey creation cancelled or invalid title.");
      }
      
      validate();  // Refresh UI
   }

   private void editSurveyOptions() {  
      if (!surveys.isEmpty()) {  
          Survey survey = surveys.get(0); // For simplicity, editing the first survey.
          
          StringBuilder optionsListBuilder = new StringBuilder();  
          for (String option : survey.getOptions()) {  
              optionsListBuilder.append(option).append(", ");  
          }  
          
          String currentOptions = optionsListBuilder.toString();  
          String updatedOptionsInput = DialogUtils.showInputDialog(this,
              "Current Options: " + currentOptions + "\nEnter new options separated by commas:", "Edit Survey Options");  
          
          if (updatedOptionsInput != null && !updatedOptionsInput.trim().isEmpty()) {  
              List<String> updatedOptions = List.of(updatedOptionsInput.split(",\\s*"));  
              survey.setOptions(updatedOptions);  
              messageLabel.setText("Updated options for survey: " + survey.getTitle());  
          } else {  
              messageLabel.setText("No changes made.");  
          }  
          
      } else {  
          messageLabel.setText("No surveys available to edit.");  
      }  

      validate();  
   }

    private void respondToSurvey() {
      if (!surveys.isEmpty()) {
          Survey survey = surveys.get(0); // Responding to the first survey for simplicity

          CheckboxGroup group = new CheckboxGroup(); 
          Panel optionPanel = new Panel(); 
          
          for (String option : survey.getOptions()) { 
              Checkbox checkbox = new Checkbox(option, group, false); 
              optionPanel.add(checkbox); 
          }
          
          Dialog dialog = new Dialog(this, "Respond to Survey", true); 
          dialog.setLayout(new BorderLayout()); 
          
          dialog.add(optionPanel, BorderLayout.CENTER); 
          
          Button submitResponseButton = new Button("Submit Response"); 
          submitResponseButton.addActionListener(e -> { 
              Checkbox selectedCheckbox = group.getSelectedCheckbox(); 
              if (selectedCheckbox != null) { 
                  survey.addResponse(selectedCheckbox.getLabel()); 
                  messageLabel.setText("Response submitted to survey: " + survey.getTitle()); 
              } else { 
                  messageLabel.setText("Please select an option."); 
              } 
              dialog.dispose(); 
              validate();  
              repaint();  
          }); 

          dialog.add(submitResponseButton, BorderLayout.SOUTH); 

          dialog.setSize(300, 200); 
          dialog.setVisible(true); 

      } else { 
          messageLabel.setText("No surveys available to respond."); 
      } 

      validate();  
   }

   private void viewResponses() {  
      if (!surveys.isEmpty()) {  
          StringBuilder responseList = new StringBuilder("Survey Responses:\n");  
          for (Survey survey : surveys) {  
              responseList.append(survey.getTitle()).append(": \n");  
              Map<String, Integer> responseCountMap = survey.getResponseCountMap();  
              for (String option : survey.getOptions()) {  
                  int count = responseCountMap.getOrDefault(option, 0);  
                  responseList.append(option).append(": ").append(count).append("\n");  
              }  
              responseList.append("\n");  
          }  
          messageLabel.setText(responseList.toString());  
      } else {  
          messageLabel.setText("No surveys created yet.");  
      }  

      validate();  
   }

    private void viewSurveys() {
      if (!surveys.isEmpty()) {
          StringBuilder surveyList = new StringBuilder("Available Surveys:\n");
          for (Survey survey : surveys) {
              surveyList.append(survey.getTitle()).append("\n");
          }
          messageLabel.setText(surveyList.toString());
          messageLabel.setForeground(Color.MAGENTA); // Set title color to purple/magenta.
      } else {
          messageLabel.setText("No surveys created yet.");
      }
      
      validate();  // Refresh UI
   }

    public static void main(String[] args) {
      new SurveyApplication();
   }
}

class User {
   private String username;
   private String password;
   private Role role;

   public User(String username, String password, Role role) {
      this.username = username;
      this.password = password;
      this.role = role;
   }

   public String getUsername() { return username; }
   public String getPassword() { return password; }
   public Role getRole() { return role; }
}

enum Role {
   ADMIN,
   USER
}

class Survey {
   private String title;
   private List<String> responses;
   private List<String> options;

   public Survey(String title, List<String> options) { 
      this.title = title; 
      this.responses = new ArrayList<>(); 
      this.options = options; 
   }

   public String getTitle() { return title; }
   
   public List<String> getResponses() { return responses; }

   public List<String> getOptions() { return options; } 

   public Map<String, Integer> getResponseCountMap() {  
      Map<String, Integer> responseCountMap = new HashMap<>();  
      for (String response : responses) {  
          responseCountMap.put(response, responseCountMap.getOrDefault(response, 0) + 1);  
      }  
      return responseCountMap;  
   }

   public void setOptions(List<String> updatedOptions) { this.options = updatedOptions; }

   public void addResponse(String response) { responses.add(response); }
}

// Utility class for dialog input
class DialogUtils {

    public static String showInputDialog(Frame parent, String message, String title) {
      Dialog dialog = new Dialog(parent, title, true);
      dialog.setLayout(new FlowLayout());

      Label label = new Label(message);
      TextField inputField = new TextField(20);
      Button okButton = new Button("OK");

      okButton.addActionListener(e -> dialog.dispose());

      dialog.add(label);
      dialog.add(inputField);
      dialog.add(okButton);

      dialog.setSize(300, 150);
      dialog.setVisible(true);

      return inputField.getText();  // Return the entered text after closing the dialog
    }
}
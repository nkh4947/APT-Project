package org.tizen.common.annotation.apt.util;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.resource.JFaceResources;

public class CreateSourceDialog extends ApplicationWindow{

    public String pack;
    public String author;
    public String account;
    public String name;
    public String projectName;
    public int type;
    private Label titleLabel;
    private Text messageLabel;
    private final int GROUP_WIDTH = 500;
    private final int GROUP_HEIGHT= 200;
    private final int BUTTON_WIDTH = 70;
    private Text packageText;
    private Text authorText;
    private Text accountText;
    private Text nameText;
    private Text projectNameText;
    private Combo typeCombo;
    private Button okButton;
    private boolean closedByOk = false;
    
    private String[] sourceTypes = {
            "class",
            "interface",
            "annotation",
            "enum"
    };
    
    public CreateSourceDialog(Shell parentShell, String projectName, String pack, String author, String account) {
        super(parentShell);
        this.projectName = projectName;
        this.pack = pack;
        this.author = author;
        this.account = account;
    }
    
    public boolean isClosedByOk() {
        return this.closedByOk;
    }
    
    @Override
    protected Control createContents(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginLeft = 5;
        layout.marginRight = 5;
        layout.marginTop = 5;
        layout.marginBottom = 5;
        
        mainComposite.setLayout(layout);
        SWTUtil.setGridLayoutData(mainComposite, 700, -1, -1, -1, SWT.NONE);
        createMessageText(mainComposite);
        
        this.nameText.setFocus();
        this.getShell().setDefaultButton(okButton);

        this.addListeners();
        return mainComposite;
    }
    
    private void createMessageText(Composite mainComposite) {
        titleLabel = new Label(mainComposite, SWT.LEFT);
        titleLabel.setFont(JFaceResources.getBannerFont());
        SWTUtil.setGridLayoutData(titleLabel, -1, -1, -1, -1, GridData.FILL_BOTH);

        messageLabel = new Text(mainComposite, SWT.WRAP | SWT.READ_ONLY);
        messageLabel.setText(" \n "); // two lines//$NON-NLS-1$
        messageLabel.setFont(JFaceResources.getDialogFont());
        SWTUtil.setGridLayoutData(messageLabel, -1, -1, -1, -1, GridData.FILL_BOTH);
        
        createMainComponent(mainComposite);
        createButtons(mainComposite);
        
        this.messageLabel.setMessage("Input source name");
        this.getShell().setText("Create Source");
    }

    private void createButtons(Composite mainComposite) {
        Composite buttonComposite = new Composite(mainComposite, SWT.None);
        GridLayout buttonComositeLayout = new GridLayout();
        buttonComositeLayout.numColumns = 3;
        buttonComositeLayout.makeColumnsEqualWidth = false;
        buttonComposite.setLayout(buttonComositeLayout);
        SWTUtil.setGridLayoutData(buttonComposite, -1, -1, -1, -1, GridData.FILL_HORIZONTAL);
        
        Label emptyLabel = new Label(buttonComposite, SWT.None);
        SWTUtil.setGridLayoutData(emptyLabel, -1, -1, -1, -1, GridData.FILL_HORIZONTAL);
        
        Button cancelButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);
        cancelButton.setText("Cancel");
        SWTUtil.setGridLayoutData(cancelButton, -1, BUTTON_WIDTH, -1, -1, SWT.NONE);
        cancelButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                close();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        
        okButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);
        okButton.setText("OK");
        SWTUtil.setGridLayoutData(okButton, -1, BUTTON_WIDTH, -1, -1, SWT.NONE);
        okButton.setEnabled(false);
    }

    private void addListeners() {
        this.nameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if(validateDialog()) {
                    okButton.setEnabled(true);
                }
                else {
                    okButton.setEnabled(false);
                }
            }
        });
        
        okButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                performOk();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }
    
    private void performOk() {
        
        if(!validateDialog()) {
            return;
        }
        
        this.projectName = this.getProjectName();
        this.account = this.getAccount();
        this.author = this.getAuthor();
        this.pack = this.getPackage();
        this.name = this.getName();
        this.type = this.getType();
        this.closedByOk  = true;
        this.close();
    
    }
    
    protected boolean validateDialog() {
        String name= this.getName();
        if("".equals(name)) {
            setMessage("Input source name");
            return false;
        }
        return true;
    }
    
    public String getProjectName() {
        return this.projectNameText.getText();
    }
    
    public String getName() {
        return this.nameText.getText();
    }
    
    public String getPackage() {
        return this.packageText.getText();
    }

    public String getAuthor() {
        return this.authorText.getText();
    }

    public String getAccount() {
        return this.accountText.getText();
    }
    
    public int getType() {
        
        String sourceType = this.typeCombo.getText();
        for(int i=0; i<sourceTypes.length; i++) {
            if(sourceType.equals(sourceTypes[i])) {
                return i;
            }
        }
        return 0;
    }
    private void setMessage(String message) {
        if(message == null) {
            this.messageLabel.setText("");
            return;
        }
        this.messageLabel.setText(message);
    }

    private void createMainComponent(Composite mainComposite) {

        final Composite textComposite = new Composite(mainComposite, SWT.NONE);
        GridLayout textCompositeLayout = new GridLayout();
        textCompositeLayout.numColumns = 2;
        textCompositeLayout.makeColumnsEqualWidth = false;
        textComposite.setLayout(textCompositeLayout);
        
        SWTUtil.setGridLayoutData(textComposite, GROUP_HEIGHT, GROUP_WIDTH, -1, -1, GridData.FILL_BOTH);

        createLabel(textComposite, "Project name: ");
        this.projectNameText = new Text(textComposite, SWT.BORDER);
        createText(this.projectNameText, this.projectName);
        
        createLabel(textComposite, "Author: ");
        this.authorText = new Text(textComposite, SWT.BORDER);
        createText(this.authorText, this.author);
        
        createLabel(textComposite, "Account: ");
        this.accountText = new Text(textComposite, SWT.BORDER);
        createText(this.accountText, this.account);
        
        createLabel(textComposite, "Package: ");
        this.packageText = new Text(textComposite, SWT.BORDER);
        createText(this.packageText, this.pack);
        
        createLabel(textComposite, "Source name: ");
        this.nameText = new Text(textComposite, SWT.BORDER);
        createText(this.nameText, "");
        
        createLabel(textComposite, "Source type");
        this.typeCombo = new Combo(textComposite, SWT.BORDER | SWT.READ_ONLY | SWT.DROP_DOWN);
        SWTUtil.setGridLayoutData(typeCombo, -1, -1, -1, -1, GridData.FILL_HORIZONTAL);
        
        for(String type: sourceTypes) {
            this.typeCombo.add(type);
        }
        this.typeCombo.setText(sourceTypes[0]);
    }

    private void createText(Text text, String title) {
        SWTUtil.setGridLayoutData(text, -1, -1, -1, -1, GridData.FILL_HORIZONTAL);
        text.setText(title);
    }

    private void createLabel(Composite parent, String title) {
        Label label = new Label(parent, SWT.None);
        label.setText(title);
    }

}

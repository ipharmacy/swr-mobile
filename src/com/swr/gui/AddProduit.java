/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swr.gui;

import com.codename1.capture.Capture;
import com.codename1.components.InfiniteProgress;
import com.codename1.components.InteractionDialog;
import com.codename1.components.SpanLabel;
import com.codename1.components.ToastBar;
import com.codename1.ext.filechooser.FileChooser;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.io.MultipartRequest;
import com.codename1.io.NetworkManager;
import com.codename1.io.Util;
import com.codename1.ui.Button;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.List;
import com.codename1.ui.TextField;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.swr.entities.Categorie;
import java.util.ArrayList;
import com.swr.services.ServiceCategorie;
import com.codename1.util.StringUtil;
import com.swr.entities.SessionUser;
import com.swr.entities.produit;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;
/**
 *
 * @author Dhia
 */
public class AddProduit extends BaseForm {
    Form current; 
    Resources theme= UIManager.initFirstTheme("/theme");
    private ArrayList<Categorie> categories;
    com.swr.services.ServiceCategorie SC = new ServiceCategorie();
    
    private String im ;
    
    AddProduit(Resources theme) {
        setTitle("Ajouter un produit");
        setLayout(new BorderLayout());
        
        Container textfields = new Container(BoxLayout.y()); 
        TextField tnom = new TextField("","Nom",20, TextField.ANY);
        TextField tquant = new TextField("","Quantite",20, TextField.NUMERIC);
        TextField tprix = new TextField("","Prix",20, TextField.NUMERIC);
        
        categories=SC.getAllcategories();
        ComboBox<Categorie> comb = new ComboBox();

        for (int i = 0; i < categories.size(); i++) {
            comb.addItem(categories.get(i));
        }
      
        textfields.addAll(new Label("Nom du produit"),tnom);
        textfields.addAll(new Label("Quantite du produit"),tquant);
        textfields.addAll(new Label("prix par unité"),tprix);
        textfields.addAll(new Label("Categorie"),comb);
        
        
        Style s = UIManager.getInstance().getComponentStyle("TitleCommand");
        FontImage icone = FontImage.createMaterial(FontImage.MATERIAL_IMAGE, s);
        Button upload = new Button("Upload Image",icone);
        
        upload.addActionListener((e)->{
               try {
                    String fileNameInServer = "";
                    MultipartRequest cr = new MultipartRequest();
                    String filepath = Capture.capturePhoto(-1, -1);
                    cr.setUrl("http://localhost:8080/uploadimage.php");
                    cr.setPost(true);
                    String mime = "image/jpeg";
                    cr.addData("file", filepath, mime);;
                    String out = new com.codename1.l10n.SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                    cr.setFilename("file", out + ".jpg");//any unique name you want

                    fileNameInServer += out + ".jpg";
                    System.err.println("path2 =" + fileNameInServer);
                    im =fileNameInServer ;
                    InfiniteProgress prog = new InfiniteProgress();
                    Dialog dlg = prog.showInifiniteBlocking();
                    cr.setDisposeOnCompletion(dlg);
                    NetworkManager.getInstance().addToQueueAndWait(cr);
                } catch ( IOException ex) {

                }
        });
 
         this.getToolbar().addMaterialCommandToLeftBar("Back", FontImage.MATERIAL_ARROW_BACK, (e) -> {
            Produits l = new Produits(this,theme);
            l.show();
        });
        
        Button add = new Button("Ajouter produit");
        
        add.addActionListener((e)->{
            int test=0;
            System.out.println("IMAGE NAME "+im);
            
            if (tnom.getText() =="" || tprix.getText()=="" || tquant.getText()=="" || im == null ||Float.parseFloat(tprix.getText())<=0  || Integer.parseInt(tquant.getText())<=0   ) {
            InteractionDialog dlg = new InteractionDialog("Erreur d'ajout");
            dlg.setLayout(new BorderLayout());
            dlg.add(BorderLayout.CENTER, new SpanLabel("Veuillez remplir tous les champs avec des valeurs logique"));
            Button close = new Button("Close");
            close.addActionListener((ee) -> dlg.dispose());
            dlg.addComponent(BorderLayout.SOUTH, close);
            Dimension pre = dlg.getContentPane().getPreferredSize();
            dlg.show(50, 100, 30, 30);
            return;
        }
            
            
           /* if(tnom.getText() =="" || tprix.getText()=="" || tquant.getText()=="" || im == null ||Float.parseFloat(tprix.getText())<=0  || Integer.parseInt(tquant.getText())<=0     ){
                test=1;
                ToastBar.showMessage("verifier les champs ", FontImage.MATERIAL_INFO); 
            }
            if(test==0)
            {*/
                  produit p = new produit();
            p.setIdUtilisateur(SessionUser.loggedUser.getId());
            p.setImage_name(im);
            p.setNom(tnom.getText());
            p.setPrix(Float.parseFloat(tprix.getText()));
            p.setQuantite(Integer.parseInt(tquant.getText()));
            
            if(com.swr.services.ServiceProduit.getInstance().addproduit(p,comb.getSelectedItem().getId())){
                System.out.println("added produit");
                Produits a =  new Produits(this,theme);
                a.showBack();
            }
            
          
            
            ;
        });
        
        add(BorderLayout.NORTH,textfields);
        //add(BorderLayout.CENTER,upload);
        Container ff = new Container(BoxLayout.x());
        ff.addAll(add,upload);
        add(BorderLayout.SOUTH,ff);
        
        
        
        
        
        
        
    }


  
}

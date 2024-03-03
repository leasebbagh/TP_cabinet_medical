package sio.tp2;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import sio.tp2.Model.RendezVous;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class TP2Controller implements Initializable {

    private TreeMap<String, TreeMap<String, RendezVous>> monPlanning;
    TreeItem root;
    @FXML
    private TextField txtNomPatient;
    @FXML
    private ComboBox cboNomPathologie;
    @FXML
    private TreeView tvPlanning;
    @FXML
    private DatePicker dpDateRdv;
    @FXML
    private Spinner spHeure;
    @FXML
    private Spinner spMinute;
    @FXML
    private Button cmdValider;

    @FXML
    public void cmdValiderClicked(Event event) {
        if(txtNomPatient.getText().equals(""))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("");
            alert.setContentText("Veuillez saisir le nom du patient");
            alert.showAndWait();
        }
        else if(dpDateRdv.getValue()==null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("");
            alert.setContentText("Veuillez choisir une date");
            alert.showAndWait();
        }
        else
        {
            // On récupère l'heure avec les JSPINNER
            String heureChoisie = "";
            String minuteChoisie = "";
            heureChoisie = spHeure.getValue().toString();
            if(spHeure.getValue().toString().length() == 1)
            {
                heureChoisie = "0"+spHeure.getValue().toString();
            }
            minuteChoisie = spMinute.getValue().toString();

            if(spMinute.getValue().toString().length() == 1)
            {
                minuteChoisie = spMinute.getValue().toString() + "0";
            }
            String heureRdv = heureChoisie + ":" + minuteChoisie;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dateRdv = dtf.format(dpDateRdv.getValue());

            if(RechercherRDV(dateRdv, heureRdv))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de RDV");
                alert.setHeaderText("");
                alert.setContentText("Il existe déjà un RDV à cette date et à cette heure");
                alert.showAndWait();
            }
            else
            {
                RendezVous rdv = new RendezVous(heureRdv, txtNomPatient.getText(), cboNomPathologie.getSelectionModel().getSelectedItem().toString());
                if(!monPlanning.containsKey(dateRdv))
                {
                    TreeMap<String,RendezVous> lesRdv = new TreeMap<String,RendezVous>();
                    monPlanning.put(dateRdv, lesRdv );
                }

                monPlanning.get(dateRdv).put(heureRdv, rdv);

                // Mise à jour du JTREE
                TreeItem noeudDate = null;
                TreeItem noeudHeure = null;
                TreeItem noeudRdv = null;
                // on supprime tous les noeuds sauf la racine
                root.getChildren().clear();
                for (String dte : monPlanning.keySet())
                {
                    noeudDate = new TreeItem(dte);
                    noeudDate.setExpanded(true);
                    for (String heure : monPlanning.get(dte).keySet())
                    {
                        noeudHeure = new TreeItem(heure);
                        noeudHeure.setExpanded(true);
                        noeudRdv = new TreeItem(monPlanning.get(dte).get(heure).getNomPatient());
                        noeudHeure.getChildren().add(noeudRdv);
                        noeudRdv = new TreeItem(monPlanning.get(dte).get(heure).getNomPathologie());
                        noeudHeure.getChildren().add(noeudRdv);
                        noeudDate.getChildren().add(noeudHeure);

                    }
                    root.getChildren().add(noeudDate);
                }
                root.setExpanded(true);
                tvPlanning.setRoot(root);
            }

        }
    }

    public boolean RechercherRDV(String dateRdv,String heureRdv)
    {
        boolean trouve  = false;

        if(monPlanning.containsKey(dateRdv))
        {
            if(monPlanning.get(dateRdv).containsKey(heureRdv))
            {
                trouve = true;
            }
        }
        return trouve;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        monPlanning = new TreeMap<>();
        root = new TreeItem("Mon planning");
        tvPlanning.setRoot(root);

        cboNomPathologie.getItems().addAll("Angine","Grippe","Covid","Gastro");
        cboNomPathologie.getSelectionModel().selectFirst();

        SpinnerValueFactory spinnerValueFactoryHeure = new SpinnerValueFactory.IntegerSpinnerValueFactory(8,19,8,1);
        spHeure.setValueFactory(spinnerValueFactoryHeure);
        SpinnerValueFactory spinnerValueFactoryMinute = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,45,0,15);
        spMinute.setValueFactory(spinnerValueFactoryMinute);
    }
}
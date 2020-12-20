package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.*;
import java.util.Scanner;

public class Controller {
    @FXML
    Button chooser;
    @FXML
    Button plot;
    @FXML
    Button A;
    @FXML
    Button T;
    @FXML
    Button G;
    @FXML
    Button C;
    @FXML
    RadioButton AT;
    @FXML
    RadioButton GC;
    @FXML
    Button find_ori;
    @FXML
    TextField FileOpen;
    @FXML
    TextField FileDesc;
    @FXML
    TextArea seq_disp;
    @FXML
    TextField seq_len;
    @FXML
    TextField A_perc;
    @FXML
    TextField T_perc;
    @FXML
    TextField G_perc;
    @FXML
    TextField C_perc;
    @FXML
    TextField ori;
    @FXML
    Pane plt;
    double countA,countT,countG,countC;
    int seqLen=0;
    int oric,ter;
    int diffGC[];
    int diffAT[];
    double ATskew[];
    double GCskew[];
    public void chooseFile(ActionEvent e) throws FileNotFoundException {
        System.out.println("CHOSEN");
        FileChooser ch=new FileChooser();
        ch.setInitialDirectory(new File("src"));
        File file=ch.showOpenDialog(Main.guiStage);
        readFile(file);
    }
    public void readFile(File file) throws FileNotFoundException {
        FileOpen.setText(file.getAbsolutePath());
        Scanner in=new Scanner(new BufferedReader(new FileReader(file.getAbsolutePath())));
        in.next();
        FileDesc.setText(in.nextLine().substring(1));
        StringBuilder sequence=new StringBuilder("");
//        int seqLen=0;
        seqLen=0;
        while(in.hasNext()){
            String s=in.nextLine();
            seqLen+=s.length();
            sequence.append(s+'\n');
        }
        countPerc(sequence.toString());
        seq_len.setText(String.valueOf(seqLen));
        seq_disp.setStyle("-fx-font-family: Consolas;-fx-font-size: 14;");
        seq_disp.setText(sequence.toString());
        skewInitialise(sequence.toString());
    }
    public void countPerc(String seq){
        countA=0;countT=0;countG=0;countC=0;
        for(int i=0;i<seq.length();i++){
            if(seq.charAt(i)=='A')
                countA++;
            else if(seq.charAt(i)=='T')
                countT++;
            else if(seq.charAt(i)=='G')
                countG++;
            else
                countC++;
        }
        A_perc.setText(String.valueOf(countA*100/seq.length()));
        T_perc.setText(String.valueOf(countT*100/seq.length()));
        G_perc.setText(String.valueOf(countG*100/seq.length()));
        C_perc.setText(String.valueOf(countC*100/seq.length()));
    }
    public void changeA(ActionEvent e){
        if(A.getText().equals("A%")){
            A_perc.setText(String.valueOf((int)countA));
            A.setText("A");
        }
        else{
            A_perc.setText(String.valueOf(countA*100/seqLen));
            A.setText("A%");
        }
    }
    public void changeT(ActionEvent e){
        if(T.getText().equals("T%")){
            T_perc.setText(String.valueOf((int)countT));
            T.setText("T");
        }
        else{
            T_perc.setText(String.valueOf(countT*100/seqLen));
            T.setText("T%");
        }
    }
    public void changeG(ActionEvent e){
        if(G.getText().equals("G%")){
            G_perc.setText(String.valueOf((int)countG));
            G.setText("G");
        }
        else{
            G_perc.setText(String.valueOf(countG*100/seqLen));
            G.setText("G%");
        }
    }
    public void changeC(ActionEvent e){
        if(C.getText().equals("C%")){
            C_perc.setText(String.valueOf((int)countC));
            C.setText("C");
        }
        else{
            C_perc.setText(String.valueOf(countC*100/seqLen));
            C.setText("C%");
        }
    }
    public void plot_skew(ActionEvent e){
        if(AT.isSelected()==true){
            Pane pl=drawSkew(ATskew);
            if(plt.getChildren().size()!=0)
                plt.getChildren().remove(plt.getChildren().size()-1);
            plt.getChildren().add(pl);
        }
        else if(GC.isSelected()==true){
            Pane pl=drawSkew(GCskew);
            if(plt.getChildren().size()!=0)
                plt.getChildren().remove(plt.getChildren().size()-1);
            plt.getChildren().add(pl);
        }
    }
    public void skewInitialise(String s){
        Pane p=new Pane();
        int GCdiff[]=new int[s.length()];
        int ATdiff[]=new int[s.length()];
        int ATsum[]=new int[s.length()];
        int GCsum[]=new int[s.length()];
        int c_count=0;
        int g_count=0,a_count=0,t_count=0;
        int min_diff=Integer.MAX_VALUE;
        int max_diff=Integer.MIN_VALUE;
        int min_index=-1;
        int max_index=-1;
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)=='G'){
                g_count++;
            }
            else if(s.charAt(i)=='C'){
                c_count++;
            }
            else if(s.charAt(i)=='A'){
                a_count++;
            }
            else
                t_count++;

            GCsum[i]=g_count+c_count;
            GCdiff[i]=g_count-c_count;
            ATsum[i]=a_count+t_count;
            ATdiff[i]=a_count-t_count;
//            System.out.print(GCdiff[i]+",");
            if(GCdiff[i]<min_diff) {
                min_diff = GCdiff[i];
                min_index=i;
            }
            if(GCdiff[i]>max_diff){
                max_diff=GCdiff[i];
                max_index=i;
            }
        }
//        System.out.print("ORI: "+min_index+" ");
//        System.out.println("DIFF %: "+min_diff);
//        System.out.print("TER: "+max_index+" ");
//        System.out.println("DIFF %: "+max_diff);

        // Code for constructing graph
//        NumberAxis x_axis=new NumberAxis();
//        NumberAxis y_axis=new NumberAxis();
//        LineChart<Number,Number> bc =new LineChart<>(x_axis,y_axis);
//        bc.setTitle("Skew plot");
//        bc.setAnimated(false);
//        bc.setCreateSymbols(false);
//        XYChart.Series series=new XYChart.Series();
//        final int consta=10;
//        XYChart.Data ar[]=new XYChart.Data[s.length()/consta];
//        for(int i=0;i<ar.length;i+=1) {
//            ar[i]=new XYChart.Data(i*consta,GCdiff[i*consta]);
//        }
//        if(ar[ar.length-1]==null)
//            ar[ar.length-1]=new XYChart.Data(0,0);
//        series.getData().addAll(ar);
//        bc.getData().add(series);
//        System.out.println("SRS");
//        p.getChildren().add(bc);
        int consta=10;
        ATskew=new double[s.length()/consta];
        GCskew=new double[s.length()/consta];

        for(int i=0;i<ATskew.length;i++){
            double Atsumavg=0,Gcsumavg=0,Atdiffavg=0,Gcdiffavg=0;
            int count=0;
            for(int j=0;j<10;j++){
                if(i*consta+j<ATsum.length) {
                    Atsumavg += ATsum[i * consta + j];
                    Gcsumavg += GCsum[i * consta + j];
                    Atdiffavg += ATdiff[i * consta + j];
                    Gcdiffavg += GCdiff[i * consta + j];
                    count++;
                }
            }
            Atsumavg/=count;
            Atdiffavg/=count;
            Gcsumavg/=count;
            Gcdiffavg/=count;
            ATskew[i]=Atdiffavg;//Atsumavg;
            GCskew[i]=Gcdiffavg;//Gcsumavg;
//                if(ATsum[i]!=0)
//                ATskew[i]=(double)ATdiff[i*consta]/(double)ATsum[i*consta];
//                if(GCsum[i]!=0)
//                GCskew[i]=(double)GCdiff[i*consta]/(double)GCsum[i*consta];
        }

        //define globals for these including ATskew GCskew
        oric=min_index;
        ter=max_index;

        diffGC=GCdiff;
        diffAT=ATdiff;
    }
    public Pane drawSkew(double diff[]){
        Pane p=new Pane();
        NumberAxis x_axis=new NumberAxis();
        NumberAxis y_axis=new NumberAxis();
        LineChart<Number,Number> bc =new LineChart<>(x_axis,y_axis);
        bc.setTitle("Skew plot");
        bc.setAnimated(false);
        bc.setCreateSymbols(false);
        XYChart.Series series=new XYChart.Series();
        final int consta=10;

        XYChart.Data ar[]=new XYChart.Data[diff.length];
        for(int i=0;i<ar.length;i+=1) {
            ar[i]=new XYChart.Data(i*consta,diff[i]);
        }

        series.getData().addAll(ar);
        bc.getData().add(series);
        System.out.println("SRS");
        p.getChildren().add(bc);
        return p;
    }
    public void display_ori(ActionEvent e){
        ori.setText(String.valueOf(oric));
    }


}

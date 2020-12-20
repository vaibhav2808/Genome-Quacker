package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.*;
import java.util.ArrayList;
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
    CheckBox Reverse;
    @FXML
    CheckBox Complement;
    @FXML
    Button Find;
    @FXML
    Button Extract;
    @FXML
    Button extract_save;
    @FXML
    CheckBox Complement_also;
    @FXML
    Button pattern_save;
    @FXML
    TextField FileOpen;
    @FXML
    TextField FileDesc;
    @FXML
    TextArea seq_disp;
    @FXML
    TextArea ext_seq_disp;
    @FXML
    TextArea pattern_details;
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
    TextField Start;
    @FXML
    TextField End;
    @FXML
    TextField save_extract;
    @FXML
    TextField Enter_pattern;
    @FXML
    TextField save_pattern;
    @FXML
    Pane plt;
    String seq;
    String ext_seq;
    String pattern_result;
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
            sequence.append(s);
        }
        seq=sequence.toString();
        countPerc(sequence.toString());
        seq_len.setText(String.valueOf(seqLen));
        seq_disp.setStyle("-fx-font-family: Consolas;-fx-font-size: 14;");
        seq_disp.setWrapText(true);
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

            GCdiff[i]=g_count-c_count;
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
                if(i*consta+j<ATdiff.length) {
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
    public void perf_extract(ActionEvent E){
        ext_seq=extractSequence(seq,(Integer.valueOf(Start.getText())-1),(Integer.valueOf(End.getText())-1),Reverse.isSelected(),Complement.isSelected());
        ext_seq_disp.setWrapText(true);
        ext_seq_disp.setStyle("-fx-font-family: Consolas;-fx-font-size: 14;");
        ext_seq_disp.setText(ext_seq);
    }
    public String extractSequence(String s,int start,int end,boolean reversed,boolean complement){
        String str=s.substring(start,end+1);
        if(reversed)
            str=new StringBuilder().append(str).reverse().toString();
        if(complement)
            str=complement(str);
        return str;
    }
    public String complement(String reverse){
        char arr[]=reverse.toCharArray();
        StringBuilder str=new StringBuilder(arr.length);
        for(int i=0;i<arr.length;i++){
            if(arr[i]=='A')
                str.append('T');
            else if(arr[i]=='T')
                str.append('A');
            else if(arr[i]=='G')
                str.append('C');
            else
                str.append('G');
        }
        return str.toString();
    }
    public void save_ext(ActionEvent e) throws IOException {
        BufferedWriter bw=new BufferedWriter(new FileWriter("savedFiles/"+save_extract.getText()+".txt"));
        bw.write(ext_seq);
        bw.close();
    }
    public void searchPattern(ActionEvent e){
        pattern_result=patternSearch(seq,Enter_pattern.getText(),Complement_also.isSelected());
        pattern_details.setWrapText(true);
        pattern_details.setStyle("-fx-font-family: Consolas;-fx-font-size: 14;");
        pattern_details.setText(pattern_result);
    }
    public String patternSearch(String text,String pattern,boolean reverseComplement){
        StringBuilder s=new StringBuilder("");
        s.append("Pattern: "+Enter_pattern.getText()+"\n\n");
        s.append("Normal Strand: ");
        ArrayList<Integer> pos=new ArrayList<>(patternSearchHelper(text, pattern));
        int i;
        for(i=0;i<pos.size();i++){
            s.append(pos.get(i)+",");
        }
        if(reverseComplement){
            pos.addAll(patternSearchHelper(text, reverseComplement(pattern)));
            s.append("\n\nComplement Strand: ");
            for(;i<pos.size();i++){
                s.append(pos.get(i)+",");
            }
        }
        return s.toString();
    }
    public ArrayList<Integer> patternSearchHelper(String text, String pattern){
        ArrayList<Integer> positions=new ArrayList<>();
        int i=0,temp;
        while(true){
            temp=text.indexOf(pattern,i);
            if(temp==-1)
                break;
            positions.add(temp);
            i=temp+1;
        }
        return positions;
    }
    public String reverseComplement(String forward){
        String reverse=new StringBuilder().append(forward).reverse().toString();
        return complement(reverse);
    }
    public void save_pattern_result(ActionEvent e) throws IOException {
        BufferedWriter bw=new BufferedWriter(new FileWriter("savedFiles/"+save_pattern.getText()+".txt"));
        bw.write(pattern_result);
        bw.close();
    }

}

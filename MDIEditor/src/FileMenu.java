
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.text.BadLocationException;

public class FileMenu extends JFrame {

    MDIEditor MDIEditor;
    AudioPlayer openYee = new AudioPlayer();//轉檔音效控制項
    java.net.URL Yee = MDIEditor.class.getResource("/voice/Yee.aiff");//取得Yee.aiff的URL

    FileMenu(JMenu mnFile, MDIEditor MDIEditor) {
        this.MDIEditor = MDIEditor;
        JMenuItem miNew = new JMenuItem("新增(N)", KeyEvent.VK_N),
                miOpen = new JMenuItem("開啟舊檔(O)", KeyEvent.VK_O),
                miSave = new JMenuItem("儲存檔案(S)", KeyEvent.VK_S),
                miSaveAn = new JMenuItem("另存新檔(A)", KeyEvent.VK_A),
                miYee = new JMenuItem("PDF轉TXT(Y)", KeyEvent.VK_Y),
                miToPDF = new JMenuItem("TXT轉PDF(T)", KeyEvent.VK_T),
                miExit = new JMenuItem("結束(E)", KeyEvent.VK_E);
        //宣告檔案功能表的選項

        miNew.addActionListener(alFile); //為功能表選項加上監聽器
        miOpen.addActionListener(alFile);
        miSave.addActionListener(alFile);
        miSaveAn.addActionListener(alFile);
        miYee.addActionListener(alFile);
        miToPDF.addActionListener(alFile);
        miExit.addActionListener(alFile);

        mnFile.add(miNew); //將選項加入檔案功能表
        mnFile.add(miOpen);
        mnFile.add(miSave);
        mnFile.add(miSaveAn);
        mnFile.addSeparator();
        mnFile.add(miYee);
        mnFile.add(miToPDF);
        mnFile.addSeparator();
        mnFile.add(miExit);
    }

    //定義並宣告回應檔案功能表內選項被選取所觸發事件的監聽器
    ActionListener alFile = (ActionEvent e) -> {
        int result;
        try {
            //執行檔案開啟動作
            switch (e.getActionCommand()) {
                case "開啟舊檔(O)": {
                    JFileChooser fcOpen = new JFileChooser(
                            MDIEditor.internalEditor.tifCurrent.getFilePath());
                    //宣告JFileChooser物件
                    NewFileFilter fileFilter = new NewFileFilter("Text & PDF Files", new String[]{"txt", "pdf"});
                    fcOpen.addChoosableFileFilter(fileFilter);
                    fcOpen.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    //設定篩選檔案的類型
                    fcOpen.setAcceptAllFileFilterUsed(false);
                    fcOpen.setDialogTitle("開啟舊檔"); //設定檔案選擇對話盒的標題
                    result = fcOpen.showOpenDialog(MDIEditor);
                    //顯示開啟檔案對話盒
                    if (result == JFileChooser.APPROVE_OPTION) { //使用者按下 確認 按鈕
                        File file = fcOpen.getSelectedFile(); //取得選取的檔案
                        String fi[] = {file.getPath()};//抓出字串array
                        String fe;
                        fe = fi[0].substring(fi[0].length() - 3, fi[0].length());//抓尾
                        switch (fe.toLowerCase()) {
                            case "txt":
                                MDIEditor.internalEditor.createInternalFrame(file.getPath(), file.getName());
                                //以取得的檔案建立TextInternalFrame物件
                                break;
                            case "pdf":
                                PDFViewer pv = new PDFViewer(file.getPath());
                                break;
                        }
                    }
                    break;
                }
                case "新增(N)":
                    //新增文件
                    MDIEditor.internalEditor.createInternalFrame(); //建立沒有內容的TextInternalFrame物件
                    break;
                case "儲存檔案(S)":
                    //執行儲存檔案動作
                    String strPath = MDIEditor.internalEditor.tifCurrent.getFilePath();
                    //取得目前TextInternalFrame物件開啟檔案的路徑與名稱
                    if (!MDIEditor.internalEditor.tifCurrent.isNew()) {
                        //判斷TextInternalFrame物件開啟的是否為新的檔案
                        FileWriter fw = new FileWriter(strPath);
                        //建立輸出檔案的FileWriter物件
                        MDIEditor.internalEditor.tifCurrent.write(fw);
                    } else {
                        MDIEditor.saveFile(strPath); //儲存檔案
                    }
                    break;
                case "另存新檔(A)":
                    MDIEditor.saveFile(MDIEditor.internalEditor.tifCurrent.getFilePath()); //儲存檔案
                    break;
                case "PDF轉TXT(Y)":
                    JFileChooser fcOpen = new JFileChooser(MDIEditor.internalEditor.tifCurrent.getFilePath());
                    //宣告JFileChooser物件 
                    NewFileFilter fileFilter = new NewFileFilter("PDF Files", new String[]{"pdf"});
                    fcOpen.addChoosableFileFilter(fileFilter);
                    fcOpen.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    //設定篩選檔案的類型
                    fcOpen.setAcceptAllFileFilterUsed(false);
                    fcOpen.setDialogTitle("選擇要轉檔的PDF"); //設定檔案選擇對話盒的標題
                    result = fcOpen.showOpenDialog(MDIEditor);
                    //顯示開啟檔案對話盒
                    if (result == JFileChooser.APPROVE_OPTION) { //使用者按下 確認 按鈕
                        File file = fcOpen.getSelectedFile(); //取得選取的檔案
                        ExtractText extractor = new ExtractText();
                        String filePathArray[]={file.getPath()};
                        extractor.startExtraction(filePathArray);
                        try {
                            openYee.loadAudio(Yee);//載入yee
                        } catch (Exception YeeException) {
                            System.out.println(YeeException.toString());
                        }
                        openYee.play();
                        System.setProperty("apple.awt.UIElement", "true");
                        String FP, FN;
                        FP = file.getPath().substring(0, file.getPath().length() - 3) + "txt";
                        FN = file.getName().substring(0, file.getName().length() - 3) + "txt";
                        MDIEditor.internalEditor.createInternalFrame(FP, FN);//以取得的檔案建立TextInternalFrame物件
                    }
                    break;
                case "TXT轉PDF(T)":
                    TextToPDF textToPDF = new TextToPDF();
                    break;
                case "結束(E)":
                    MDIEditor.Exit();
                    break;
            }
        } catch (IOException ioe) {
            System.err.println(ioe.toString());
        } catch (BadLocationException ble) {
            System.err.println("位置不正確\n" + ble.toString());
        }
    };
}

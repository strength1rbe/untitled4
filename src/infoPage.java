import javax.swing.*;
import java.awt.*;

public class infoPage extends JPanel {
    JLabel tutorial;
    infoPage(){
        this.setPreferredSize(new Dimension(500,500));
        this.setLayout(null);
        this.setFocusable(true);
        this.requestFocus();
        tutorial = new JLabel(("<html>"
                + "ברוכים הבאים למשחק יריות בחלל!<br>"
                + "במשחק המהיר והמרגש הזה, אתה שולט במטוס קרב בקרב נגד גלים של מטוסי אויב בחלל החיצון.<br>"
                + "השתמש במקש החץ הימני כדי להזיז את המטוס ימינה, ובמקש החץ השמאלי כדי להזיז אותו שמאלה.<br>"
                + "המטוס שלך יורה כדורים באופן אוטומטי.<br>"
                + "המטרה היא לפגוע במטוסי האויב עם הכדורים שלך.<br>"
                + "מטוסי האויב מופיעים בחלק העליון של המסך ונעים כלפי מטה.<br>"
                + "כל פגיעה מורידה חיים מהאויב, וכשהחיים מגיעים לאפס, מטוס האויב מושמד.<br>"
                + "המשחק מסתיים כאשר כל מטוסי האויב מושמדים.<br>"
                + "הודעת 'סיום משחק' תופיע והחלון ייסגר.<br>"
                + "בהצלחה במשחק!"
                + "</html>"));
        tutorial.setBounds(50,50,350,350);
        this.add(tutorial);
        JButton start = new JButton();
        start.setBounds(175,400,150,80);
        start.setText("Start");
        start.addActionListener(e -> {// e , (event)
            closeWindow();
            new GameFrame(new GamePanel());
        });
        this.add(start);
    }
    public void closeWindow(){
        Window window = SwingUtilities.getWindowAncestor(this);
        window.dispose();
    }
}

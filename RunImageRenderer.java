import ecs100.*;

public class RunImageRenderer{

    public static void main(String[] args){
        ImageRenderer ir = new ImageRenderer();
        UI.initialise();
        UI.addButton("Clear", UI::clearGraphics );
        UI.addButton("Render ", ir::renderImage );
        
        UI.addButton("Quit", UI::quit );
        UI.setWindowSize(850, 700);
        UI.setDivider(0.0);
    }
}

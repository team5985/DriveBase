package frc.robot.auto;

import frc.robot.AutoController.AutoSelection;

import frc.robot.subsystems.Drive;

public class DummyAuto extends AutoMode {
    private String name = "Dummy auto template";
    private AutoSelection autoType = AutoSelection.DUMMYAUTO;
    
    @Override
    public boolean getExit() {
        return false;
    }

    @Override
    public void init() {
        
    }

    @Override
    public boolean runStep(int step) {
        switch(step) {
            case 0:
           // System.out.println("happy :)))");
            Drive.getInstance().arcadeDrive(0.5, 0, 1);
            break;
        }

        return false;
    }

}
package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PWMSpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Robot;

public class Drive extends Subsystem{

    PWMSpeedController LeftDrive;
    PWMSpeedController RightDrive;
    Encoder LeftEnc;
    Encoder RightEnc;


    public static Drive driveInstance;
    
    public static Drive getInstance() {
        if (driveInstance == null) {
            driveInstance = new Drive();
        }
        return driveInstance;
    }


    public void setSystem(PWMSpeedController leftDrive, PWMSpeedController rightDrive, Encoder rightEnc, Encoder leftEnc) {
        LeftDrive = leftDrive;
        RightDrive = rightDrive;
        LeftEnc = leftEnc;
        RightEnc = rightEnc;
    }
        

    //Drive control

    public void setMotors(double leftPower, double rightPower) {
        //set motors
        LeftDrive.set(leftPower);

        RightDrive.set(rightPower);
      
    }

    public void arcadeDrive(double throttle, double steering, double power) {
        //Left
        double leftPower = (power + steering) * throttle;
        //Right
        double rightPower = (power - steering) * throttle;
        //Write to motors
        setMotors(leftPower, -rightPower);
        System.out.println("LEFT" + LeftEnc.getDistance());
        System.out.println("RIGHT" + RightEnc.getDistance());
        
    }

    public void update() {

    }    

    @Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
    }

}
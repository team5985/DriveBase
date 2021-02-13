/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.auto.AutoMode;
import frc.robot.subsystems.Drive;
import edu.wpi.first.wpilibj.I2C;
import frc.robot.BasicMotorCheck;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    private static final String kDefaultAuto = "Default";
    private static final String kCustomAuto = "My Auto";
    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    AutoController m_AutoController;

    Drive drive = Drive.getInstance();

    Spark LeftDrive = new Spark(7);
    Spark RightDrive = new Spark(8);



    Encoder LeftEnc = new Encoder(1, 2); 
    Encoder RightEnc = new Encoder(3, 4);

    Joystick joystick = new Joystick(0);
    double steerDirection = 0;
    double power = 0;
    double throttle = 0;

    UltrasonicI2C usi2cl;
    UltrasonicI2C usi2cr;
    boolean usTrigger = false;
    boolean usRevButton = false;
    double totalDistanceTravelled = 0;
    double encoderDistance = 0;
    PowerDistributionPanel PDP = new PowerDistributionPanel(0);
    

    BasicMotorCheck checkLeftDrive1 = new BasicMotorCheck(LeftDrive,0 , PDP);
    BasicMotorCheck checkLeftDrive2 = new BasicMotorCheck(LeftDrive,1 , PDP);

    BasicMotorCheck checkRightDrive1 = new BasicMotorCheck(RightDrive,14, PDP);
    BasicMotorCheck checkRightDrive2 = new BasicMotorCheck(RightDrive,15, PDP);


    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
        drive.setSystem(LeftDrive, RightDrive, RightEnc, LeftEnc);
        m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
        m_chooser.addOption("My Auto", kCustomAuto);
        SmartDashboard.putData("Auto choices", m_chooser);
        
        I2C.Port i2cp = I2C.Port.kOnboard;
        I2C usLinkl = new I2C(i2cp, 0x13);
        I2C usLinkr = new I2C(i2cp, 0x14);
        usi2cl = new UltrasonicI2C(usLinkl);
        usi2cr = new UltrasonicI2C(usLinkr);
    }
    /**
     * This function is called every robot packet, no matter the mode. Use
     * this for items like diagnostics that you want ran during disabled,
     * autonomous, teleoperated and test.
     *
     * <p>This runs after the mode specific periodic functions, but before
     * LiveWindow and SmartDashboard integrated updating.
     */
    @Override
    public void robotPeriodic() {
    }

    /**
     * This autonomous (along with the chooser code above) shows how to select
     * between different autonomous modes using the dashboard. The sendable
     * chooser code works with the Java SmartDashboard. If you prefer the
     * LabVIEW Dashboard, remove all of the chooser code and uncomment the
     * getString line to get the auto name from the text box below the Gyro
     *
     * <p>You can add additional auto modes by adding additional comparisons to
     * the switch structure below with additional strings. If using the
     * SendableChooser make sure to add them to the chooser code above as well.
     */
    @Override
    public void autonomousInit() {
        m_autoSelected = m_chooser.getSelected();
        // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
        System.out.println("Auto selected: " + m_autoSelected);
        m_AutoController.getInstance().initialiseAuto();
    }

    /**
     * This function is called periodically during autonomous.
    */
    @Override
    public void autonomousPeriodic() {
        usi2cl.update();
        usi2cr.update();
        m_AutoController.getInstance().runAuto();
    }
  

    /**
     * This function is called once when teleop is enabled.
     */
    @Override
    public void teleopInit() {
    }

    double delta;
    double lastError = 0;
    double outSpeed = 0;
    boolean useRightSensor = false;

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
        checkLeftDrive1.update();
        checkLeftDrive2.update();
        checkRightDrive1.update();
        checkRightDrive2.update();
        

        SmartDashboard.putNumber("LeftDrive1 Check", checkLeftDrive1.BasicMotorCheck());
        SmartDashboard.putNumber("LeftDrive2 Check", checkLeftDrive2.BasicMotorCheck());
        SmartDashboard.putNumber("RightDrive1 Check", checkRightDrive1.BasicMotorCheck());
        SmartDashboard.putNumber("RightDrive2 Check", checkRightDrive2.BasicMotorCheck());

        // 0 1 14 15
        SmartDashboard.putNumber("0", PDP.getCurrent(0));
        SmartDashboard.putNumber("1", PDP.getCurrent(1));
        SmartDashboard.putNumber("14", PDP.getCurrent(14));
        SmartDashboard.putNumber("15", PDP.getCurrent(15));
        usi2cl.update();
        usi2cr.update();
        SmartDashboard.putNumber("BadCRC", usi2cl.myCountBadCRC);
        SmartDashboard.putNumber("BadMeasurement", usi2cl.myCountBadMeas);
        encoderDistance = (LeftEnc.getDistance() + RightEnc.getDistance()) / 2;
                
        if (joystick.getTriggerPressed()) {
            usTrigger = !usTrigger;
            usRevButton = false;
            outSpeed = 0;
            LeftEnc.reset();
            RightEnc.reset();
        }
        if (joystick.getRawButtonPressed(2)) {
            if (usTrigger) {
                usRevButton = !usRevButton;
            } 
            LeftEnc.reset();
            RightEnc.reset();
        }
        if (joystick.getRawButtonPressed(7)) {
            useRightSensor = !useRightSensor;
        }
        SmartDashboard.putBoolean("Trigger", usTrigger);
        SmartDashboard.putBoolean("Reverse", usRevButton);
        UltrasonicI2C.usResults resultsr = usi2cr.getResults();
        double resr;
        if (resultsr == null) {
            resr = 0;
        }
        else {
            resr = resultsr.getResult();
        }
        SmartDashboard.putNumber("Distance right", resr);
        UltrasonicI2C.usResults resultsl = usi2cl.getResults();
        double resl;
        if (resultsl == null) {
            resl = 0;
        }
        else {
            resl = resultsl.getResult();
        }
        SmartDashboard.putNumber("Distance left", resl);

        double aimPos = 300; // how far away from the wall we want to be in mm
        double pgain = 0.00025; // how fast we correct ourselves
        double dgain = 0.005; // change in gain
        double speed = 1;
        double leftPower;
        double rightPower;
        pgain = 0.00025;
        dgain = 0.005;
        speed = 1;
        double accRate = 0.05;

        if (usTrigger)
        {
            double power = speed;
            if (usRevButton)
            {
                power = -speed;
            }

            outSpeed = outSpeed + Math.min( Math.max((power - outSpeed), -accRate), accRate);

            double dirPGain = pgain;
            double dirDGain = dgain;
            if (outSpeed < 0)
            {
                dirPGain = -dirPGain;
                dirDGain = -dirDGain;
            }
            double error = 0;
            if (useRightSensor)
            {
                error = resultsr.getResult() - aimPos; // how far off from aimPos we are
            }
            else   
            {
                error = aimPos - resultsl.getResult(); // how far off from aimPos we are
            }
            if ((useRightSensor && resultsr.getNew()) || (!useRightSensor && resultsl.getNew()))
            {
                delta = error - lastError; // the change between error and lastError
                lastError = error;
            }
            steerDirection = (error * dirPGain) + (delta * dirDGain);
            double pOutput = error * dirPGain;
            double dOutput = delta * dirDGain;
            SmartDashboard.putNumber("pOutput", pOutput);
            SmartDashboard.putNumber("dOutput", dOutput);
            SmartDashboard.putNumber("Error", error);
            leftPower = outSpeed - steerDirection;
            rightPower = steerDirection + outSpeed;
            steerPriority(leftPower, rightPower);
        }

        else {
            if (power >= -0.25 && power <= 0.25) {
                power = 0;
            }
            if (steerDirection >= -0.025 && steerDirection <= 0.025) {
                steerDirection = 0;
            }
            throttle = (-1 * joystick.getThrottle() + 1) / 2;
            steerDirection = joystick.getX() * throttle;
            power = -1 * joystick.getY() * throttle;
            steerPriority(power - steerDirection, steerDirection + power);
            steerDirection = 0;
            SmartDashboard.putNumber("steerDirection", steerDirection);
        }
    }

private void steerPriority(double left, double right)
{
    if (left - right > 2)
    {
        left = 1;
        right = -1;
    }
    else if (right - left > 2)
    {
        left = -1;
        right = 1;
    }
    else if (Math.max(right, left) > 1)
    {
        left = left - (Math.max(right,left) - 1);
        right = right - (Math.max(right,left) - 1);
    }
    else if (Math.min(right, left) < -1)
    {
        left = left - (Math.min(right,left) + 1);
        right = right - (Math.min(right,left) + 1);
    }
    SmartDashboard.putNumber("leftPower", left);
    SmartDashboard.putNumber("rightPower", left);
    SmartDashboard.putNumber("SteerLeft", left-right);
    LeftDrive.set(-left);
    RightDrive.set(right);
}

    /**
     * This function is called once when the robot is disabled.
     */
    @Override
    public void disabledInit() {
    }

    /**
     * This function is called periodically when disabled.
     */
    @Override
    public void disabledPeriodic() {
        usi2cl.update();
        usi2cr.update();
    }

    /**
     * This function is called once when test mode is enabled.
     */
    @Override
    public void testInit() {
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {
    }
}

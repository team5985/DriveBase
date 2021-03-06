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
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;

import java.io.IOException;
import java.nio.file.Path;

import javax.lang.model.util.ElementScanner6;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import frc.robot.auto.AutoMode;
import frc.robot.subsystems.Drive;
import edu.wpi.first.wpilibj.I2C;
import frc.robot.motorchecking.*;
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
    AHRS navx = new AHRS();
    

    BasicMotorCheck checkLeftDrive1 = new BasicMotorCheck(LeftDrive,0 , PDP, LeftEnc, null);
    BasicMotorCheck checkLeftDrive2 = new BasicMotorCheck(LeftDrive,1 , PDP, LeftEnc, null);
    BasicMotorCheck checkRightDrive1 = new BasicMotorCheck(RightDrive,14, PDP, RightEnc, null);
    BasicMotorCheck checkRightDrive2 = new BasicMotorCheck(RightDrive,15, PDP, RightEnc, null);

    // for *your* robot's drive.
    // The Robot Characterization Toolsuite provides a convenient tool for obtaining these
    // values for your robot.
    public static final double ksVolts = 2.34;
    public static final double kvVoltSecondsPerMeter = 2.53;
    public static final double kaVoltSecondsSquaredPerMeter = 0.2;

    public static final double kPDriveVel = 2.67;
    public static final double kDDriveVel = 0.228;

    public static final double kTrackwidthMeters = 0.6365917184943187;
    public static final DifferentialDriveKinematics kDriveKinematics =
        new DifferentialDriveKinematics(kTrackwidthMeters);

    public static final double kRamseteZeta = 0.7;


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
    public void robotPeriodic()
    {
        SmartDashboard.putNumber("POV", joystick.getPOV());
        System.out.println("POV - " + joystick.getPOV());
    }

    Trajectory trajectory = new Trajectory();
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
        /*m_autoSelected = m_chooser.getSelected();
        // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
        System.out.println("Auto selected: " + m_autoSelected);
        m_AutoController.getInstance().initialiseAuto();*/
        //String pathDir = "C:/Users/Graham/Documents/Pathweaver_test/PathWeaver/output/Unnamed.path";
        String pathDir = "paths/Unnamed.wpilib.json";
        try {
            Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(pathDir);
            trajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
         } catch (IOException ex) {
            DriverStation.reportError("Unable to open trajectory: " + pathDir, ex.getStackTrace());
         }
    }

    /**
     * This function is called periodically during autonomous.
    */
    @Override
    public void autonomousPeriodic() {
        usi2cl.update();
        usi2cr.update();
        /*m_AutoController.getInstance().runAuto();*/

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
    boolean ultrasonicWallFollower = true;
    boolean povControl = true;

    double leftEncDist;
    double rightEncDist;
    double encoderDist;

    // Variables for the sequenced hybrid controller
    boolean sequencedHybrid = false;
    int stepNo = 0;
    double startDist = -1;
    double stepTwoAngle = 0;
    double stepTwoDist = 0;                
    double steerCommand = 0;
    double angleErr;

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
        // left 27028.5
        // right 27025
        // Distanc e travelled = 6142mm
        // Calibration constant = 4.400317 counts per mm
        // So 4.4 counts / mm is accurate to 1mm every 10m!
        checkLeftDrive1.update();
        checkLeftDrive2.update();
        checkRightDrive1.update();
        checkRightDrive2.update();

        SmartDashboard.putNumber("Encoder Distance", getEncoderPos());
        
        SmartDashboard.putNumber("leftenc", LeftEnc.getDistance());
        SmartDashboard.putNumber("rightnc", RightEnc.getDistance());


        SmartDashboard.putNumber("LeftDrive1 Check", checkLeftDrive1.getStatus());
        SmartDashboard.putNumber("LeftDrive2 Check", checkLeftDrive2.getStatus());
        SmartDashboard.putNumber("RightDrive1 Check", checkRightDrive1.getStatus());
        SmartDashboard.putNumber("RightDrive2 Check", checkRightDrive2.getStatus());

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
                
        SmartDashboard.putNumber("Gyro", navx.getYaw());
        if (joystick.getTriggerPressed()) {
            usTrigger = !usTrigger;
            usRevButton = false;
            outSpeed = 0;
            LeftEnc.reset();
            RightEnc.reset();

            stepNo = 0;
        }
        if (joystick.getRawButtonPressed(2)) {
            if (usTrigger) {
                usRevButton = !usRevButton;
            } 
        }
        if (joystick.getRawButtonPressed(7)) {
            useRightSensor = !useRightSensor;
        }
        if (joystick.getRawButtonPressed(8)) {
            LeftEnc.reset();
            RightEnc.reset();
        }
        if (joystick.getRawButtonPressed(12)) {
            navx.reset();
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
        SmartDashboard.putNumber("Step Number", stepNo);
        SmartDashboard.putNumber("steerCommand", steerCommand);

        if (povControl)
        {
            if (joystick.getPOV() == 45)
            {
                usRevButton = false;
                useRightSensor = true;
                usWallFollower();
            }
            else if (joystick.getPOV() == 135)
            {
                usRevButton = true;
                useRightSensor = true;
                usWallFollower();
            }
            else if (joystick.getPOV() == 225)
            {
                usRevButton = true;
                useRightSensor = false;
                usWallFollower();
            }
            else if (joystick.getPOV() == 315)
            {
                usRevButton = false;
                useRightSensor = false;
                usWallFollower();
           }
            else
            {
                throttle = (-1 * joystick.getThrottle() + 1) / 2;
                steerDirection = joystick.getX() * throttle;
                power = -1 * joystick.getY() * throttle;

                if (power >= -0.25 && power <= 0.25) {
                    power = 0;
                }
                if (steerDirection >= -0.025 && steerDirection <= 0.025) {
                    steerDirection = 0;
                }

                steerPriority(power - steerDirection, steerDirection + power);
                steerDirection = 0;
                SmartDashboard.putNumber("steerDirection", steerDirection);
            }
        }
        else if (usTrigger)
        {
            if (sequencedHybrid)
            {
                //Check transitions
                switch (stepNo)
                {
                    case 0: // Rotate to 0degrees on gyro for initial measurement.
                        if (Math.abs(navx.getYaw()) < 1)
                        {
                            startDist = -1;
                            stepNo = 1;
                        }
                    break;
                    case 1: // Stops robot and calculates.
                        if (startDist > 0)
                        {
                            stepNo = 2;
                        }
                    break;
                    case 2:
                        if (Math.abs(stepTwoAngle - navx.getYaw()) < 1)
                        {
                            stepNo = 3;
                        }
                    break;
                    case 3:
                        if (getEncoderPos() > stepTwoDist) {
                            stepNo = 4;
                        }
                    break;
                    case 4:
                    if (Math.abs(navx.getYaw()) < 1)
                    {
                        startDist = -1;
                        stepNo = 5;
                    }
                    break;
                    case 5:
                    break;
                    default:
                        stepNo = 0;
                    break;
                }
                double error = 0;
                switch (stepNo)
                {
                    // step 1, zeros robot
                    case 0:
                        error = 0 - navx.getYaw();
                        delta = error - lastError;
                        lastError = error;
                        steerCommand = ( 0.005 * error ) + (0.02 * delta);
                        if (steerCommand > 0) {
                            //steerCommand = Math.max(steerCommand, 0.25);
                            steerCommand = steerCommand + 0.25;
                        }
                        else if (steerCommand < 0) {
                            //steerCommand = Math.min(steerCommand, -0.25);
                            steerCommand = steerCommand - 0.25;
                        }
                        steerPriority(-steerCommand, steerCommand);
                    break;
                    // stops robot, does calc
                    case 1:
                        steerPriority(0, 0);
                        if (resultsl.getNew())
                        {
                            startDist = resultsl.getResult();
                            SmartDashboard.putNumber("startDist", startDist);
                            stepTwoAngle = -(Math.atan((startDist - 300) / 2000) / Math.PI) * 180;
                            stepTwoDist = getEncoderPos() + Math.sqrt((2000 * 2000) + ((startDist - 300)*(startDist - 300)));
                            SmartDashboard.putNumber("stepTwoDist", stepTwoDist);
                            SmartDashboard.putNumber("stepTwoAngle", stepTwoAngle);

                        }
                    break;
                    // turns to stepTwoAngle
                    case 2:
                        error = stepTwoAngle - navx.getYaw();
                        delta = error - lastError;
                        lastError = error;
                        steerCommand = ( 0.005 * error ) + (0.02 * delta);
                        steerCommand = ( 0.01 * error ) + (0.02 * delta);
                        if (steerCommand > 0) {
                            //steerCommand = Math.max(steerCommand, 0.25);
                            steerCommand = steerCommand + 0.25;
                        }
                        else if (steerCommand < 0) {
                            //steerCommand = Math.min(steerCommand, -0.25);
                            steerCommand = steerCommand - 0.25;
                        }
                        steerPriority(-steerCommand, steerCommand);
                    break;
                    // moves to stepTwoDist
                    case 3:
                        double driveAngle = stepTwoAngle;

                        error = stepTwoDist - getEncoderPos();
                        double angleErr = driveAngle - navx.getYaw();
                        double power = error * 0.001;
                        steerCommand = angleErr * 0.0005;
                        steerPriority(power - steerCommand, power + steerCommand);
                        
                    break;
                    // zeros robot, goes into usWallFollow
                    case 4:
                        error = 0 - navx.getYaw();
                        delta = error - lastError;
                        lastError = error;
                        steerCommand = ( 0.006 * error ) + (0.02 * delta);
                        steerCommand = ( 0.01 * error ) + (0.02 * delta);
                        if (steerCommand > 0) {
                            //steerCommand = Math.max(steerCommand, 0.25);
                            steerCommand = steerCommand + 0.25;
                        }
                        else if (steerCommand < 0) {
                            //steerCommand = Math.min(steerCommand, -0.25);
                            steerCommand = steerCommand - 0.25;
                        }
                        steerPriority(-steerCommand, steerCommand);

                    break;
                    case 5:
                        usWallFollower();
                    break;
                    default:
                        steerPriority(0, 0);
                    break;

                }
            }
            else if (ultrasonicWallFollower)
            {        
                usWallFollower();
                // double aimPos = 300; // how far away from the wall we want to be in mm
                // double pgain = 0.00025; // how fast we correct ourselves
                // double dgain = 0.005; // change in gain
                // double speed = 1;
                // double leftPower;
                // double rightPower;
                // pgain = 0.00025;
                // dgain = 0.005;
                // speed = 1;
                // double accRate = 0.05;

                // double power = speed;
                // if (usRevButton)
                // {
                //     power = -speed;
                // }

                // outSpeed = outSpeed + Math.min( Math.max((power - outSpeed), -accRate), accRate);

                // double dirPGain = pgain;
                // double dirDGain = dgain;
                // if (outSpeed < 0)
                // {
                //     dirPGain = -dirPGain;
                //     dirDGain = -dirDGain;
                // }
                // double error = 0;
                // if (useRightSensor)
                // {
                //     error = resultsr.getResult() - aimPos; // how far off from aimPos we are
                // }
                // else   
                // {
                //     error = aimPos - resultsl.getResult(); // how far off from aimPos we are
                // }
                // if ((useRightSensor && resultsr.getNew()) || (!useRightSensor && resultsl.getNew()))
                // {
                //     delta = error - lastError; // the change between error and lastError
                //     lastError = error;
                // }
                // steerDirection = (error * dirPGain) + (delta * dirDGain);
                // double pOutput = error * dirPGain;
                // double dOutput = delta * dirDGain;
                // SmartDashboard.putNumber("pOutput", pOutput);
                // SmartDashboard.putNumber("dOutput", dOutput);
                // SmartDashboard.putNumber("Error", error);
                // leftPower = outSpeed - steerDirection;
                // rightPower = steerDirection + outSpeed;
                // steerPriority(leftPower, rightPower);
            }
            else
            {
                steerPriority(0, -0);
            }
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

    private double getEncoderPos()
    {
        leftEncDist = LeftEnc.getDistance();
        rightEncDist = -RightEnc.getDistance();

        encoderDist = ((leftEncDist + rightEncDist) / 2 )  / 4.4;
        return encoderDist;
    }

private void usWallFollower()
    {        
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

        double aimPos = 300; // how far away from the wall we want to be in mm
        double pgain = 0.00025; // how fast we correct ourselves
        double dgain = 0.005; // change in gain
        dgain = 0.007;
        double speed = 1;
        double leftPower;
        double rightPower;
        pgain = 0.00025;
        dgain = 0.005;
        dgain = 0.005;
        pgain = 0.00025;
        double accRate = 0.05;
        accRate = 0.08;

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
        leftEncDist = LeftEnc.getDistance();
        rightEncDist = -RightEnc.getDistance();
        SmartDashboard.putNumber("Left Raw Encoder", leftEncDist);
        SmartDashboard.putNumber("Right Raw Encoder", rightEncDist);
    
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

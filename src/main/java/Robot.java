import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.POVButton;
import edu.wpi.first.wpilibj.command.Scheduler;


public class Robot extends TimedRobot {
    Robot() {
        super(.01);
    }



    private static Joystick controller = new Joystick(0);
    public static ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(controller);

    private double currentTime;
    private double startTime;

    private JoystickButton a = new JoystickButton(controller, 1);
    private JoystickButton x = new JoystickButton(controller,3);
    private POVButton up = new POVButton(controller,0);
    private POVButton down = new POVButton(controller, 180);
    private String fileName = "ElevatorDataOutput" + ElevatorSubsystem.getElevatorPercent();

    private ElevatorCommand elevatorCommand = new ElevatorCommand();
    private BikeBrakeDisengagedCommand bikeBrakeDisengagedCommand = new BikeBrakeDisengagedCommand();
    private BikeBrakeEngagedCommand bikeBrakeEngagedCommand = new BikeBrakeEngagedCommand();
    private GraphCSV graphCSV = new GraphCSV(fileName,"Velocity","Position","time");

    @Override
    public void robotInit() {
        startTime = System.currentTimeMillis()/1000d;
        elevatorSubsystem.init();
        a.toggleWhenPressed(elevatorCommand);
    }


    @Override
    public void disabledInit() {
        graphCSV.writeToFile();
    }

    @Override
    public void robotPeriodic() {
        Scheduler.getInstance().run();
    }


    @Override
    public void disabledPeriodic() {
    }


    @Override
    public void autonomousPeriodic() {


    }


    @Override
    public void teleopPeriodic() {
        currentTime = System.currentTimeMillis()/1000d;
        graphCSV.updateMainFile(elevatorSubsystem.getVelocity(),elevatorSubsystem.currentPosition(),currentTime - startTime);
    }


    @Override
    public void testPeriodic() {
    }

    public static ElevatorSubsystem getElevatorSubsystem(){
        return elevatorSubsystem;
    }

}




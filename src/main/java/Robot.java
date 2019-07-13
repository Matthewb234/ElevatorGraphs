import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.TimedRobot;


public class Robot extends TimedRobot {
    Robot() {
        super(.01);
    }


    private TalonSRX talon = new TalonSRX(0);
    private Joystick controller = new Joystick(0);


    @Override
    public void robotInit() {
    }




    @Override
    public void robotPeriodic() {


    }


    @Override
    public void disabledPeriodic() {


    }


    @Override
    public void autonomousPeriodic() {


    }


    @Override
    public void teleopPeriodic() {


    }


    @Override
    public void testPeriodic() {
    }
}

}

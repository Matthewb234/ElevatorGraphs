import com.team2073.common.command.AbstractLoggingCommand;

public class BikeBrakeDisengagedCommand extends AbstractLoggingCommand {

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
    @Override
    protected void executeDelegate() {
        Robot.getElevatorSubsystem().setBikeBrakeDisengaged(true);
    }


    @Override
    protected void endDelegate() {
        Robot.getElevatorSubsystem().setBikeBrakeDisengaged(false);
    }
}

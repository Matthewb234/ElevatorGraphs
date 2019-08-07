import com.team2073.common.command.AbstractLoggingCommand;

public class BikeBrakeEngagedCommand extends AbstractLoggingCommand {
    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
    @Override
    protected void executeDelegate() {
        Robot.getElevatorSubsystem().setBikeBrakeEngaged(true);
    }


    @Override
    protected void endDelegate() {
        Robot.getElevatorSubsystem().setBikeBrakeEngaged(false);
    }

}

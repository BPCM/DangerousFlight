package cx.ath.fota.dangerousFlight.persistance;

import cx.ath.fota.dangerousFlight.model.DFlier;
import cx.ath.fota.dangerousFlight.plugin.DangerousFlight;

import javax.persistence.PersistenceException;

public class PersistenceDatabase implements Persistence {

    public final DangerousFlight dangerousFlight;

    public PersistenceDatabase(final DangerousFlight dangerousFlight) {
        this.dangerousFlight = dangerousFlight;
        checkDDL();
    }

    private void checkDDL() {
        try {
            dangerousFlight.getDatabase().find(DFlier.class).findRowCount();
        } catch (PersistenceException e) {
            dangerousFlight.installDDL();
        }
    }

    @Override
    public DFlier findByName(final String playerName) {
        return dangerousFlight.getDatabase().find(DFlier.class).where().eq("playerName", playerName).findUnique();
    }

    @Override
    public void update(DFlier dFlier) {
        dangerousFlight.getDatabase().update(dFlier);
    }

    @Override
    public void save(DFlier dFlier) {
        dangerousFlight.getDatabase().save(dFlier);
    }

    @Override
    public void saveOrUpdate(DFlier dFlier) {
        DFlier dFlier1;
        if ((dFlier1 = findByName(dFlier.getPlayerName())) != null) {
            dFlier1.setFlying(dFlier.getFlying());
            dFlier1.setFlightEnabled(dFlier.getFlightEnabled());
            update(dFlier1);
        } else save(dFlier);
    }


}

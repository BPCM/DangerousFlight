package cx.ath.fota.dangerousFlight.persistance;

import cx.ath.fota.dangerousFlight.model.DFlier;
import cx.ath.fota.dangerousFlight.plugin.DangerousFlight;

import javax.persistence.PersistenceException;
import java.util.UUID;

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
    public DFlier findBuUUID(final UUID uuid) {
        return dangerousFlight.getDatabase().find(DFlier.class).where().eq("uuid", uuid).findUnique();
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
        if ((dFlier1 = findBuUUID(dFlier.getUuid())) != null) {
            dFlier1.setFlying(dFlier.getFlying());
            dFlier1.setFlightEnabled(dFlier.getFlightEnabled());
            update(dFlier1);
        } else save(dFlier);
    }
}

package cx.ath.fota.dangerousFlight.persistance;

import cx.ath.fota.dangerousFlight.model.DFlier;

import java.util.UUID;

public interface Persistence {
    DFlier findBuUUID(UUID uuid);

    void update(DFlier dFlier);

    void save(DFlier dFlier);

    void saveOrUpdate(DFlier dFlier);
}

package cx.ath.fota.dangerousFlight.persistance;

import cx.ath.fota.dangerousFlight.model.DFlier;

public interface Persistence {
    DFlier findByName(String playerName);
    void update(DFlier dFlier);
    void save(DFlier dFlier);
    void saveOrUpdate(DFlier dFlier);
}

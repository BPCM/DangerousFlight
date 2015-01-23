package cx.ath.fota.dangerousFlight.model;


import org.bukkit.entity.Player;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;


@Entity
@Table(name = "dangerousFlight")
@SuppressWarnings("UnusedDeclaration")
public class DFlier implements Serializable {
    private static final long serialVersionUID = 1039415277400116126L;
    Long id;
    UUID uuid;
    Boolean flightEnabled;
    Boolean flying;

    public DFlier(Player player) {
        this.flying = player.isFlying();
        this.flightEnabled = player.getAllowFlight();
        this.uuid = player.getUniqueId();
    }

    public DFlier() {
        this.flying = false;
        this.flightEnabled = false;
        this.uuid = null;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(unique = true, name = "uuid")
    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Column
    public Boolean getFlightEnabled() {
        return this.flightEnabled;
    }

    public void setFlightEnabled(Boolean flightEnabled) {
        this.flightEnabled = flightEnabled;
    }

    @Column
    public Boolean getFlying() {
        return this.flying;
    }

    public void setFlying(Boolean flying) {
        this.flying = flying;
    }

    @Override
    public String toString() {
        return String.format("DFlier:%s;%s;%s", uuid, flightEnabled, flying);
    }
}


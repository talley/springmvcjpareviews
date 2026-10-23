package com.talleyouro.springmvcjpademo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// Spring Boot 4.x package. On Spring Boot 3.x use: org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.persistence.EntityManager;

/**
 * Repository slice test: runs against in-memory H2, and every test is rolled back automatically.
 */
@DataJpaTest
class ShipperRepositoryTest {

    @Autowired
    private ShipperRepository repo;

    @Autowired
    private EntityManager em;

    /** Push pending SQL to the database and clear the cache, so the next read really hits the DB. */
    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    private Shipper newShipper(String name, String phone) {
        Shipper s = new Shipper(name, phone, "System");
        return repo.save(s);
    }

    // ---------- CREATE NEW SHIPPER----------

    @Test
    void save_generatesIdAndPersistsAllFields() {
        Shipper saved = newShipper("Speedy Express", "(503) 555-9838");
        flushAndClear();

        assertThat(saved.getShipperId()).isNotNull();

        Shipper found = repo.findById(saved.getShipperId()).orElseThrow();
        assertThat(found.getCompanyName()).isEqualTo("Speedy Express");
        assertThat(found.getPhone()).isEqualTo("(503) 555-9838");
        assertThat(found.getAddedBy()).isEqualTo("System");
        assertThat(found.getAddedOn()).isEqualTo(LocalDate.now());
        assertThat(found.getEditedOn()).isNull();
        assertThat(found.getEditedBy()).isNull();
    }

    @Test
    void save_assignsDifferentIdsToDifferentShippers() {
        Shipper a = newShipper("A Co", "111");
        Shipper b = newShipper("B Co", "222");

        assertThat(a.getShipperId()).isNotEqualTo(b.getShipperId());
    }

    @Test
    void save_rejectsCompanyNameLongerThan40Characters() {
        Shipper tooLong = new Shipper("X".repeat(41), "123", "System");

        assertThatThrownBy(() -> repo.saveAndFlush(tooLong))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // ---------- READ ----------

    @Test
    void findById_returnsEmptyWhenMissing() {
        Optional<Shipper> result = repo.findById((short) 32000);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_returnsEverySavedShipper() {
        newShipper("USPS", "1");
        newShipper("UPS", "2");
        newShipper("DHL", "3");
        flushAndClear();

        assertThat(repo.findAll())
                .extracting(Shipper::getCompanyName)
                .containsExactlyInAnyOrder("USPS", "UPS", "DHL");
    }

    @Test
    void findByCompanyNameContainingIgnoreCase_matchesPartialNamesInAnyCase() {
        newShipper("Speedy Express", "1");
        newShipper("USPS", "2");
        newShipper("UPS", "3");
        flushAndClear();

        List<Shipper> express = repo.findByCompanyNameContainingIgnoreCase("EXPRESS");
        assertThat(express).extracting(Shipper::getCompanyName).containsExactly("Speedy Express");

        List<Shipper> ps = repo.findByCompanyNameContainingIgnoreCase("ps");
        assertThat(ps).extracting(Shipper::getCompanyName).containsExactlyInAnyOrder("USPS", "UPS");

        assertThat(repo.findByCompanyNameContainingIgnoreCase("nomatch")).isEmpty();
    }

    // Remove this test if you deleted findTopByOrderByShipperIdDesc() from the repository.
    @Test
    void findTopByOrderByShipperIdDesc_returnsHighestId() {
        newShipper("First", "1");
        Shipper last = newShipper("Last", "2");
        flushAndClear();

        Shipper top = repo.findTopByOrderByShipperIdDesc();

        assertThat(top.getShipperId()).isEqualTo(last.getShipperId());
        assertThat(top.getCompanyName()).isEqualTo("Last");
    }

    // ---------- UPDATE ----------

    @Test
    void save_updatesAnExistingShipper() {
        Shipper saved = newShipper("Old Name", "111");
        flushAndClear();

        Shipper loaded = repo.findById(saved.getShipperId()).orElseThrow();
        loaded.setCompanyName("New Name");
        loaded.setPhone("999");
        loaded.setEditedBy("Tester");
        loaded.setEditedOn(LocalDate.now());
        repo.save(loaded);
        flushAndClear();

        Shipper reloaded = repo.findById(saved.getShipperId()).orElseThrow();
        assertThat(reloaded.getCompanyName()).isEqualTo("New Name");
        assertThat(reloaded.getPhone()).isEqualTo("999");
        assertThat(reloaded.getEditedBy()).isEqualTo("Tester");
        assertThat(reloaded.getEditedOn()).isEqualTo(LocalDate.now());
        assertThat(reloaded.getAddedBy()).isEqualTo("System");      // untouched fields survive
        assertThat(repo.count()).isEqualTo(1);                      // updated, not duplicated
    }

    // ---------- DELETE ----------

    @Test
    void deleteById_removesTheShipper() {
        Shipper saved = newShipper("To Delete", "000");
        Short id = saved.getShipperId();
        flushAndClear();
        assertThat(repo.existsById(id)).isTrue();

        repo.deleteById(id);
        flushAndClear();

        assertThat(repo.existsById(id)).isFalse();
        assertThat(repo.findById(id)).isEmpty();
    }

    @Test
    void deleteById_leavesOtherShippersAlone() {
        Shipper keep = newShipper("Keep", "1");
        Shipper drop = newShipper("Drop", "2");
        flushAndClear();

        repo.deleteById(drop.getShipperId());
        flushAndClear();

        assertThat(repo.findAll()).extracting(Shipper::getShipperId).containsExactly(keep.getShipperId());
    }
}

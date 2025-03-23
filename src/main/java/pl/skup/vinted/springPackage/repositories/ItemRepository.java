package pl.skup.vinted.springPackage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.skup.vinted.dataBase.ItemTable;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemTable, Long> {

}

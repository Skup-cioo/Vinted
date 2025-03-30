package pl.skup.vinted.springPackage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import pl.skup.vinted.dataBase.ItemTable;

@Repository
@Component
public interface ItemRepository extends JpaRepository<ItemTable, Long> {

}

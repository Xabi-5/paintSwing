import java.util.List;
import java.util.Optional;

public interface DAO<T,K> {
    List<T> getAll (Optional <K> idClaveForanea);

    int save(T t, Optional<K> idClaveForanea);

    int delete(T t);

    public int deleteById(K id);

    int deleteAll();

    boolean update(T t);

    public Optional<T> get(K tt);
}

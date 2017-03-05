package dao;

import exceptions.DeleteException;
import exceptions.ItemExistException;
import exceptions.NoItemToUpdateException;

import java.util.List;

public interface DAO<T, ID> {
    public void save(T item) throws ItemExistException;

    public void delete(T item) throws DeleteException;

    public void update(T item) throws NoItemToUpdateException;

    public T getById(ID id);

    public List<T> getAll();

    public List<T> findByName(String name);
}

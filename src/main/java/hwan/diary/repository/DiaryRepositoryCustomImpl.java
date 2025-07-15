package hwan.diary.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

public class DiaryRepositoryCustomImpl implements DiaryRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void update(long id, String text) {
        em.createQuery("update Diary d set d.text = :text where d.id = :id").setParameter("text", text).setParameter("id", id).executeUpdate();
    }

}

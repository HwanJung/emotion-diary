package hwan.diary.domain.diary.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

public class DiaryRepositoryCustomImpl implements DiaryRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void update(long id, String content) {
        em.createQuery("update Diary d set d.content = :text where d.id = :id").setParameter("text", content).setParameter("id", id).executeUpdate();
    }

}

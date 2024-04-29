package com.campusconnect.backend.favorite.repository;

import com.campusconnect.backend.favorite.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>, FavoriteRepositoryCustom {

}

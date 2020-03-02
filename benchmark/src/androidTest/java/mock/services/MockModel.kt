package mock.services

import com.example.kitprotocol.db.entity.MovieEntity

fun getMockEntity(id: Int = 0) = MovieEntity(id, "mock", "mock", 0.0, 0, "mock", "mock", "mock", "mock")
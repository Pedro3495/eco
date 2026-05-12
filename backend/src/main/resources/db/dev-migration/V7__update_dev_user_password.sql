update users
set password_hash = '$2a$10$ETQILVUAlhc5BojkA67DK.WdKwuxlKm525b.jm3cQ58XetKNzjCrm',
    updated_at = now()
where email = 'dev@eco.com';

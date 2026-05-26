# pgvector Setup and Internals

pgvector adds a `vector` column type to PostgreSQL.

Typical table:

```sql
create extension if not exists vector;

create table rag_chunks (
  id text primary key,
  document_id text not null,
  chunk_index int not null,
  title text not null,
  source text not null,
  content text not null,
  embedding vector(768) not null
);
```

Similarity search:

```sql
select *
from rag_chunks
order by embedding <=> '[0.1,0.2,...]'::vector
limit 5;
```

`<=>` is cosine distance. Lower distance means more similar. Application responses usually convert it into a relevance score.

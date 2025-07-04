// src/pages/PostList.js
import React, { useState, useEffect } from "react";

const DUMMY_CATEGORIES = [
  { name: "ALL", displayName: "전체" },
  { name: "RPG", displayName: "RPG" },
  { name: "FPS", displayName: "FPS" },
  // ...추가
];

// 예시 데이터
const DUMMY_POSTS = [
  {
    id: 1,
    title: "레전드 아이템 팝니다",
    sellerNickname: "홍길동",
    categoryDisplayName: "RPG",
    tradeStatus: "TRADE",
    isLiked: false,
    likeCount: 2,
    price: 120000,
  },
  // ...더 추가
];

function PostList() {
  // 상태 정의
  const [posts, setPosts] = useState([]);
  const [categories] = useState(DUMMY_CATEGORIES);
  const [selectedCategory, setSelectedCategory] = useState("ALL");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");

  // 데이터 fetch 예시 (실제 서버 연동 시 useEffect로 API 요청)
  useEffect(() => {
    setPosts(DUMMY_POSTS);
  }, []);

  // 필터 적용
  const filteredPosts = posts.filter(post => {
    if (selectedCategory !== "ALL" && post.categoryDisplayName !== selectedCategory) return false;
    if (minPrice && post.price < Number(minPrice)) return false;
    if (maxPrice && post.price > Number(maxPrice)) return false;
    return true;
  });

  // 좋아요 토글
  const handleLike = (postId) => {
    setPosts(posts =>
        posts.map(post =>
            post.id === postId
                ? {
                  ...post,
                  isLiked: !post.isLiked,
                  likeCount: post.isLiked ? post.likeCount - 1 : post.likeCount + 1,
                }
                : post
        )
    );
    // 실제 API 연동 시 fetch('/likes/${postId}', {method: 'POST'}) ...
  };

  return (
      <section style={{ backgroundColor: "#f8f9fa", padding: "1.5rem", borderRadius: "0.5rem" }}>
        <h2 className="mb-4">최근 게시글</h2>

        {/* 가격/카테고리 필터 */}
        <form className="row mb-3" onSubmit={e => e.preventDefault()}>
          <div className="col-md-3">
            <input type="number" className="form-control" placeholder="최소 가격 (원)"
                   value={minPrice} onChange={e => setMinPrice(e.target.value)} />
          </div>
          <div className="col-md-3">
            <input type="number" className="form-control" placeholder="최대 가격 (원)"
                   value={maxPrice} onChange={e => setMaxPrice(e.target.value)} />
          </div>
          <div className="col-md-3">
            <button type="button" className="btn btn-outline-primary w-100"
                    onClick={() => {/* 실제 서버에 검색 요청 가능 */}}>검색</button>
          </div>
          <div className="btn-group mb-3" role="group">
            {categories.map(cat => (
                <button
                    type="button"
                    className={`btn btn-outline-secondary${selectedCategory === cat.displayName ? " active" : ""}`}
                    key={cat.name}
                    onClick={() => setSelectedCategory(cat.displayName)}
                >
                  {cat.displayName}
                </button>
            ))}
          </div>
        </form>

        {/* 게시글 리스트 */}
        <div className="list-group">
          {filteredPosts.map(post => (
              <a
                  key={post.id}
                  href={`/posts/${post.id}`}
                  className="list-group-item list-group-item-action mb-2 p-3 border rounded bg-white position-relative"
                  style={{ textDecoration: "none" }}
              >
                <h5 className="mb-1 fw-bold">{post.title}</h5>
                <p className="mb-1 text-muted">{post.sellerNickname}</p>
                <p className="mb-1">
                  <span className="badge bg-secondary">{post.categoryDisplayName}</span>
                  {post.tradeStatus === "TRADE" && (
                      <span className="badge bg-secondary ms-1">거래가능</span>
                  )}
                  {post.tradeStatus === "COMPLETE" && (
                      <span className="badge bg-success ms-1">거래완료</span>
                  )}
                  {post.tradeStatus === "CANCEL" && (
                      <span className="badge bg-secondary ms-1">거래가능</span>
                  )}
                </p>
                <div className="position-absolute top-0 end-0 m-2">
                  <button
                      type="button"
                      className={`btn btn-sm border-0 bg-transparent like-button ${post.isLiked ? "text-danger" : "text-muted"}`}
                      onClick={e => { e.preventDefault(); e.stopPropagation(); handleLike(post.id); }}
                  >
                    {post.isLiked ? "❤️" : "🤍"} {post.likeCount}
                  </button>
                </div>
                <p className="mb-0 fw-semibold text-success">
                  {post.price != null ? post.price.toLocaleString() + "원" : "가격 미정"}
                </p>
              </a>
          ))}
        </div>

        {/* 게시글 작성 버튼 */}
        <a className="btn btn-primary mt-4" href="/posts/new">게시글 작성</a>
      </section>
  );
}

export default PostList;

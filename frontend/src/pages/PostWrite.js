// src/pages/PostCreate.js
import React, { useState } from "react";

const categories = [
  { name: "RPG", displayName: "RPG" },
  { name: "FPS", displayName: "FPS" },
  { name: "TRADING", displayName: "거래" },
  // ...추가
];

function PostCreate() {
  // 입력값 상태 관리
  const [form, setForm] = useState({
    title: "",
    category: categories[0].name,
    price: "",
    description: "",
    images: [],
  });

  // 이미지 파일 선택 핸들러
  const handleFileChange = (e) => {
    setForm({ ...form, images: Array.from(e.target.files) });
  };

  // 입력값 변경 핸들러
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  // 폼 제출
  const handleSubmit = (e) => {
    e.preventDefault();
    // 실제로는 FormData 생성 후 서버에 POST 요청 필요
    const data = new FormData();
    data.append("title", form.title);
    data.append("category", form.category);
    data.append("price", form.price);
    data.append("description", form.description);
    form.images.forEach((file, idx) => {
      data.append("images", file);
    });

    // 예시: 실제 업로드는 fetch/axios로 POST
    // fetch("/api/posts", { method: "POST", body: data })

    alert("등록되었습니다! (실제 서버와 연동 필요)");
  };

  return (
      <section className="p-4 bg-light rounded" style={{ maxWidth: 600, margin: "2rem auto" }}>
        <h3 className="mb-3">게시글 작성</h3>
        <form onSubmit={handleSubmit} encType="multipart/form-data">
          <div className="mb-3">
            <label htmlFor="title" className="form-label">제목</label>
            <input
                type="text"
                className="form-control"
                id="title"
                name="title"
                value={form.title}
                onChange={handleChange}
                required
            />
            <label htmlFor="category" className="form-label mt-3">카테고리</label>
            <select
                id="category"
                name="category"
                className="form-select"
                value={form.category}
                onChange={handleChange}
                required
            >
              {categories.map((cat) => (
                  <option key={cat.name} value={cat.name}>{cat.displayName}</option>
              ))}
            </select>
          </div>
          <div className="mb-3">
            <label htmlFor="price" className="form-label">가격</label>
            <input
                type="number"
                className="form-control"
                id="price"
                name="price"
                value={form.price}
                onChange={handleChange}
                required
            />
          </div>
          <div className="mb-3">
            <label htmlFor="description" className="form-label">설명</label>
            <textarea
                className="form-control"
                id="description"
                name="description"
                rows="4"
                value={form.description}
                onChange={handleChange}
                required
            ></textarea>
          </div>
          <div className="mb-3">
            <label htmlFor="images" className="form-label">이미지 업로드</label>
            <input
                type="file"
                className="form-control"
                id="images"
                name="images"
                multiple
                onChange={handleFileChange}
                required
            />
          </div>
          <button type="submit" className="btn btn-primary">등록</button>
        </form>
      </section>
  );
}

export default PostCreate;

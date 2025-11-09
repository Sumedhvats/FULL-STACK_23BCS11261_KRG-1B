// src/pages/Analysis.jsx
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getAnalysis } from "../api";
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, Cell } from "recharts";

const COLORS = ["#2563eb", "#6366f1", "#06b6d4", "#f97316", "#ef4444", "#8b5cf6"];

export default function Analysis() {
  const { id } = useParams();
  const [analysis, setAnalysis] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) return;
    getAnalysis(id)
      .then(res => setAnalysis(res.data))
      .catch(err => {
        console.error(err);
        alert("Failed to load analysis");
      })
      .finally(()=>setLoading(false));
  }, [id]);

  if (loading) return <div className="min-h-[60vh] flex items-center justify-center text-gray-300">Loading analysis...</div>;
  if (!analysis) return <div className="min-h-[60vh] flex items-center justify-center text-gray-300">No analysis available</div>;

  const { surveyTitle, totalResponses, analysis: qlist } = analysis;

  return (
    <div className="px-4 py-10 max-w-4xl mx-auto">
      <h2 className="text-2xl font-bold mb-1">{surveyTitle || "Survey Analysis"}</h2>
      <p className="text-sm text-gray-400 mb-6">{totalResponses} responses</p>

      <div className="space-y-6">
        {qlist.map((q,i) => {
          // counts is a map option->count
          const counts = q.counts || {};
          const data = Object.entries(counts).map(([option, count], idx) => ({
            option, count: Number(count), color: COLORS[idx % COLORS.length]
          }));

          return (
            <div key={i} className="bg-white/4 border border-white/6 rounded-lg p-4">
              <h4 className="font-medium mb-3">{i+1}. {q.question}</h4>

              {data.length === 0 ? (
                <p className="text-sm text-gray-400">No answers yet.</p>
              ) : (
                <div style={{ width: '100%', height: 220 }}>
                  <ResponsiveContainer>
                    <BarChart data={data} layout="vertical" margin={{ left: 20 }}>
                      <XAxis type="number" tick={{ fill: '#cbd5e1' }} />
                      <YAxis dataKey="option" type="category" width={160} tick={{ fill: '#cbd5e1' }}/>
                      <Tooltip />
                      <Bar dataKey="count">
                        {data.map((entry, idx) => (
                          <Cell key={idx} fill={entry.color} />
                        ))}
                      </Bar>
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              )}

              {q.average && (
                <p className="text-sm text-gray-300 mt-3">Average: {Number(q.average).toFixed(2)}</p>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}

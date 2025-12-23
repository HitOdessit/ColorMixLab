//
//  LeaderboardView.swift
//  ColorMixLab iOS
//
//  Leaderboard display with time-based filtering
//

import SwiftUI
import shared

struct LeaderboardView: View {
    @StateObject private var viewModel = LeaderboardViewModel()
    @Environment(\.dismiss) var dismiss

    enum TimeFilter: String, CaseIterable {
        case today = "Today"
        case week = "Week"
        case month = "Month"
        case allTime = "All Time"
    }

    @State private var selectedFilter: TimeFilter = .allTime

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Filter Tabs
                HStack(spacing: 0) {
                    ForEach(TimeFilter.allCases, id: \.self) { filter in
                        FilterTab(
                            title: filter.rawValue,
                            isSelected: selectedFilter == filter
                        ) {
                            withAnimation(.easeInOut(duration: 0.2)) {
                                selectedFilter = filter
                            }
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
                .background(Color.gray.opacity(0.1))

                // Leaderboard List
                if getFilteredEntries().isEmpty {
                    Spacer()
                    VStack(spacing: 16) {
                        Image(systemName: "trophy.fill")
                            .font(.system(size: 60))
                            .foregroundColor(.gray.opacity(0.5))

                        Text("No scores yet")
                            .font(.system(size: 20, weight: .medium))
                            .foregroundColor(.secondary)

                        Text("Play a game to set your first score!")
                            .font(.system(size: 16))
                            .foregroundColor(.secondary)
                    }
                    Spacer()
                } else {
                    ScrollView {
                        LazyVStack(spacing: 12) {
                            ForEach(Array(getFilteredEntries().enumerated()), id: \.element.timestamp) { index, entry in
                                LeaderboardRow(
                                    rank: index + 1,
                                    entry: entry
                                )
                            }
                        }
                        .padding(16)
                    }
                }
            }
            .navigationTitle("🏆 Leaderboard")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        dismiss()
                    }
                }
            }
        }
        .onAppear {
            viewModel.loadEntries()
        }
    }

    private func getFilteredEntries() -> [LeaderboardEntry] {
        switch selectedFilter {
        case .today:
            return viewModel.todayEntries
        case .week:
            return viewModel.weekEntries
        case .month:
            return viewModel.monthEntries
        case .allTime:
            return viewModel.allTimeEntries
        }
    }
}

struct FilterTab: View {
    let title: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.system(size: 14, weight: isSelected ? .bold : .medium))
                .foregroundColor(isSelected ? .white : .primary)
                .padding(.vertical, 8)
                .padding(.horizontal, 16)
                .frame(maxWidth: .infinity)
                .background(
                    RoundedRectangle(cornerRadius: 8)
                        .fill(isSelected ? Color.blue : Color.clear)
                )
        }
    }
}

struct LeaderboardRow: View {
    let rank: Int
    let entry: LeaderboardEntry

    private var medalEmoji: String? {
        switch rank {
        case 1: return "🥇"
        case 2: return "🥈"
        case 3: return "🥉"
        default: return nil
        }
    }

    private var difficultyColor: Color {
        switch entry.difficulty {
        case .easy: return .green
        case .medium: return .orange
        case .hard: return .red
        default: return .gray
        }
    }

    var body: some View {
        HStack(spacing: 16) {
            // Rank
            ZStack {
                Circle()
                    .fill(rank <= 3 ? Color.yellow.opacity(0.2) : Color.gray.opacity(0.1))
                    .frame(width: 44, height: 44)

                if let medal = medalEmoji {
                    Text(medal)
                        .font(.system(size: 24))
                } else {
                    Text("\(rank)")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.secondary)
                }
            }

            // Player Info
            VStack(alignment: .leading, spacing: 4) {
                Text(entry.nickname)
                    .font(.system(size: 16, weight: .semibold))
                    .foregroundColor(.primary)

                HStack(spacing: 8) {
                    Text("Level \(entry.level)")
                        .font(.system(size: 13))
                        .foregroundColor(.secondary)

                    Circle()
                        .fill(Color.gray)
                        .frame(width: 3, height: 3)

                    Text(entry.difficulty.name.capitalized)
                        .font(.system(size: 13, weight: .medium))
                        .foregroundColor(difficultyColor)
                }
            }

            Spacer()

            // Score
            Text("\(entry.score)")
                .font(.system(size: 20, weight: .bold))
                .foregroundColor(rank <= 3 ? .blue : .primary)
        }
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(rank <= 3 ? Color.blue.opacity(0.05) : Color.white)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(rank == 1 ? Color.yellow : Color.clear, lineWidth: 2)
        )
    }
}

class LeaderboardViewModel: ObservableObject {
    @Published var todayEntries: [LeaderboardEntry] = []
    @Published var weekEntries: [LeaderboardEntry] = []
    @Published var monthEntries: [LeaderboardEntry] = []
    @Published var allTimeEntries: [LeaderboardEntry] = []

    private let storage = PlatformStorage()
    private lazy var leaderboardManager = LeaderboardManager(storage: storage)

    func loadEntries() {
        todayEntries = leaderboardManager.getTodayEntries(limit: 10)
        weekEntries = leaderboardManager.getWeekEntries(limit: 10)
        monthEntries = leaderboardManager.getMonthEntries(limit: 10)
        allTimeEntries = leaderboardManager.getAllTimeEntries(limit: 10)
    }
}

#Preview {
    LeaderboardView()
}
